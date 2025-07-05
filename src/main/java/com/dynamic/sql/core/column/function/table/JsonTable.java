/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.table;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.TableFunction;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.SqlUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.dynamic.sql.core.column.AbstractAliasHelper.bindAlias;


/**
 * <pre>
 * {@code
 * JSON_TABLE(
 *   expr,
 *   path COLUMNS (
 *     column_name type PATH path [DEFAULT expr ON ERROR] [DEFAULT expr ON EMPTY],
 *     ...
 *   )
 * )
 * }
 * </pre>
 * •	expr: 是一个 JSON 数据的表达式，通常是 JSON 列或者 JSON 结构。<br>
 * •	path: 是 JSON 数据的路径，指定要提取的 JSON 数据位置。<br>
 * •	COLUMNS: 用于定义结果中的列，并指定从 JSON 中提取的数据。<br>
 */
public class JsonTable extends AbstractTableFunction {
    //    private String expr;
    private String path;
    private List<JsonColumn> columns = new ArrayList<>();
    private ParameterBinder parameterBinder;

    public JsonTable(TableFunction tableFunction, String path, JsonColumn... jsonColumn) {
        super(tableFunction);
        this.path = path;
        if (jsonColumn == null || jsonColumn.length <= 0) {
            throw new IllegalArgumentException("The extracted Json column must be declared");
        }
        columns.addAll(Arrays.asList(jsonColumn));
    }

    public <T, F> JsonTable(FieldFn<T, F> fn, String path, JsonColumn... jsonColumn) {
        super(fn);
        this.path = path;
        if (jsonColumn == null || jsonColumn.length <= 0) {
            throw new IllegalArgumentException("The extracted Json column must be declared");
        }
        columns.addAll(Arrays.asList(jsonColumn));
    }

    public <T, F> JsonTable(String tableAlias, FieldFn<T, F> fn, String path, JsonColumn... jsonColumn) {
        super(bindAlias(tableAlias, fn));
        this.path = path;
        if (jsonColumn == null || jsonColumn.length <= 0) {
            throw new IllegalArgumentException("The extracted Json column must be declared");
        }
        columns.addAll(Arrays.asList(jsonColumn));
    }

    public JsonTable(String columnName, String path, JsonColumn... jsonColumn) {
        super(bindAlias(null, columnName));
        this.path = path;
        if (jsonColumn == null || jsonColumn.length <= 0) {
            throw new IllegalArgumentException("The extracted Json column must be declared");
        }
        columns.addAll(Arrays.asList(jsonColumn));
    }

    public JsonTable(String tableAlias, String columnName, String path, JsonColumn... jsonColumn) {
        super(bindAlias(tableAlias, columnName));
        this.path = path;
        if (jsonColumn == null || jsonColumn.length <= 0) {
            throw new IllegalArgumentException("The extracted Json column must be declared");
        }
        columns.addAll(Arrays.asList(jsonColumn));
    }

    @Override
    public String getFunctionToString(SqlDialect sqlDialect, Version version) throws UnsupportedOperationException {
        StringBuilder sb = new StringBuilder();
        sb.append("json_table(").append(tableFunction.getFunctionToString(sqlDialect, version))
                .append(", '").append(path).append("' columns(");

        for (int i = 0; i < columns.size(); i++) {
            sb.append(columns.get(i).toString());
            if (i < columns.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("))");
        return sb.toString();
    }

    @Override
    public ParameterBinder getParameterBinder() {
        return parameterBinder;
    }

    /**
     * <pre>
     *     {@code
     *             if (jsonColumn == null && jsonColumn.length <= 0) {
     *             throw new IllegalArgumentException("The extracted Json column must be declared");
     *         }
     *     }
     * </pre>
     */
    public static class JsonColumn {
        //提取的字段（必须）
        protected String columnName;
        //该字段对应的数据类型（必须）
        protected String dataType;
        //目标路径（必须）
        protected String jsonPath;
        //默认值
        protected Object defaultValue;
        //On条件
        protected List<OnEvent> on = new ArrayList<>();

        protected JsonColumn() {
        }

        public static JsonColumnBuilder builder() {
            return new JsonColumnBuilder(new JsonColumn());
        }

        @Override
        public String toString() {
            // product_name VARCHAR(150) PATH '$.product' DEFAULT 'Unknown' ON ERROR DEFAULT 'None' ON EMPTY,
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(columnName).append(" ").append(dataType).append(" path '").append(jsonPath).append("' ");
            if (defaultValue != null) {
                Object v = SqlUtils.formattedParameter(defaultValue);
                stringBuilder.append("default ").append(v).append(" ");
            }
            for (OnEvent onEvent : on) {
                stringBuilder.append(" on ").append(onEvent.getEvent()).append(" ");
                if (onEvent.getDefaultValue() != null) {
                    Object v = SqlUtils.formattedParameter(defaultValue);
                    stringBuilder.append("default ").append(v).append(" ");
                }
            }
            return stringBuilder.toString();
        }

        public static class OnEvent {
            protected JsonColumnBuilder jsonColumnBuilder;
            //ON ERROR 和 ON EMPTY
            private String event;
            private Object defaultValue;

            public OnEvent(JsonColumnBuilder jsonColumnBuilder) {
                this.jsonColumnBuilder = jsonColumnBuilder;
            }

            public OnEvent error() {
                event = "error";
                return this;
            }

            public OnEvent empty() {
                event = "empty";
                return this;
            }

            public OnEvent defaultValue(Object defaultValue) {
                this.defaultValue = defaultValue;
                return this;
            }

            public OnEvent on() {
                OnEvent onEvent = new OnEvent(jsonColumnBuilder);
                jsonColumnBuilder.appendOn(onEvent);
                return onEvent;
            }

            public JsonColumn build() {
                return jsonColumnBuilder.build();
            }

            protected String getEvent() {
                return event;
            }

            protected Object getDefaultValue() {
                return defaultValue;
            }
        }
    }

    public static class JsonColumnBuilder {
        private JsonColumn jsonColumn;

        public JsonColumnBuilder(JsonColumn jsonColumn) {
            this.jsonColumn = jsonColumn;
        }

        public JsonColumnBuilder column(String columnName) {
            jsonColumn.columnName = columnName;
            return this;
        }

        public JsonColumnBuilder dataType(String dataType) {
            this.jsonColumn.dataType = dataType;
            return this;
        }

        public JsonColumnBuilder jsonPath(String jsonPath) {
            this.jsonColumn.jsonPath = jsonPath;
            return this;
        }

        public JsonColumnBuilder defaultValue(Object defaultValue) {
            this.jsonColumn.defaultValue = defaultValue;
            return this;
        }

        public JsonColumn.OnEvent on() {
            JsonColumn.OnEvent onEvent = new JsonColumn.OnEvent(this);
            this.jsonColumn.on.add(onEvent);
            return onEvent;
        }

        protected void appendOn(JsonColumn.OnEvent onEvent) {
            this.jsonColumn.on.add(onEvent);
        }

        public JsonColumn build() {
            return jsonColumn;
        }
    }
}
