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
import com.dynamic.sql.core.column.function.MysqlLiteral;
import com.dynamic.sql.enums.SqlDialect;
import java.util.Locale;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.core.column.function.TableFunction;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.exception.DynamicSqlException;
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
            throw new DynamicSqlException("The extracted Json column must be declared");
        }
        columns.addAll(Arrays.asList(jsonColumn));
    }

    public <T, F> JsonTable(FieldFn<T, F> fn, String path, JsonColumn... jsonColumn) {
        super(fn);
        this.path = path;
        if (jsonColumn == null || jsonColumn.length <= 0) {
            throw new DynamicSqlException("The extracted Json column must be declared");
        }
        columns.addAll(Arrays.asList(jsonColumn));
    }

    public <T, F> JsonTable(String tableAlias, FieldFn<T, F> fn, String path, JsonColumn... jsonColumn) {
        super(bindAlias(tableAlias, fn));
        this.path = path;
        if (jsonColumn == null || jsonColumn.length <= 0) {
            throw new DynamicSqlException("The extracted Json column must be declared");
        }
        columns.addAll(Arrays.asList(jsonColumn));
    }

    public JsonTable(String columnName, String path, JsonColumn... jsonColumn) {
        super(bindAlias(null, columnName));
        this.path = path;
        if (jsonColumn == null || jsonColumn.length <= 0) {
            throw new DynamicSqlException("The extracted Json column must be declared");
        }
        columns.addAll(Arrays.asList(jsonColumn));
    }

    public JsonTable(String tableAlias, String columnName, String path, JsonColumn... jsonColumn) {
        super(bindAlias(tableAlias, columnName));
        this.path = path;
        if (jsonColumn == null || jsonColumn.length <= 0) {
            throw new DynamicSqlException("The extracted Json column must be declared");
        }
        columns.addAll(Arrays.asList(jsonColumn));
    }

    @Override
    public ParameterBinder parameterBinder() {
        return tableFunction.parameterBinder();
    }

    @Override
    public String render(RenderContext context) {
        if(context.getSqlDialect()!=SqlDialect.MYSQL
                || (context.getVersion()!=null && context.getVersion().compareTo(new Version(8,0,4))<0))
            throw new UnsupportedOperationException("JSON_TABLE requires MySQL 8.0.4 or later");
        StringBuilder sql=new StringBuilder("JSON_TABLE(").append(tableFunction.render(context))
                .append(", ").append(MysqlLiteral.text(path)).append(" COLUMNS(");
        for(int i=0;i<columns.size();i++) {
            if(i>0) sql.append(", ");sql.append(columns.get(i).toString());
        }
        return sql.append("))").toString();
    }

    /**
     * <pre>
     *     {@code
     *             if (jsonColumn == null && jsonColumn.length <= 0) {
     *             throw new DynamicSqlException("The extracted Json column must be declared");
     *         }
     *     }
     * </pre>
     */
    public static class JsonColumn {
        private boolean ordinality;
        private String nestedPath;
        private List<JsonColumn> nestedColumns;
        /** 嵌套路径继续使用受控列声明，不接受原始 SQL。 */
        public static JsonColumn nested(String path,JsonColumn... columns) {
            if(columns==null || columns.length==0) throw new IllegalArgumentException("Nested columns are required");
            JsonColumn column=new JsonColumn();column.nestedPath=path;
            column.nestedColumns=Arrays.asList(columns.clone());return column;
        }

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
            if(nestedColumns!=null) {
                StringBuilder sql=new StringBuilder("NESTED PATH ").append(MysqlLiteral.text(nestedPath)).append(" COLUMNS(");
                for(int i=0;i<nestedColumns.size();i++) {
                    if(i>0) sql.append(", ");sql.append(nestedColumns.get(i).toString());
                }
                return sql.append(")").toString();
            }
            String name=MysqlLiteral.identifier(columnName);
            if(ordinality) return name+" FOR ORDINALITY";
            String type=dataType==null ? "" : dataType.toUpperCase(Locale.ROOT).replace(" ","");
            if(!type.matches("(TINYINT|SMALLINT|MEDIUMINT|INT|INTEGER|BIGINT|DOUBLE|FLOAT|DATE|TIME|DATETIME|TIMESTAMP|JSON|BOOLEAN|DECIMAL\\([0-9]+,[0-9]+\\)|VARCHAR\\([0-9]+\\)|CHAR\\([0-9]+\\))"))
                throw new IllegalArgumentException("Unsupported JSON_TABLE column type");
            StringBuilder sql=new StringBuilder(name).append(" ").append(type).append(" PATH ").append(MysqlLiteral.text(jsonPath));
            if(defaultValue!=null) sql.append(" DEFAULT ").append(jsonDefault(defaultValue)).append(" ON EMPTY");
            for(String event:Arrays.asList("empty","error")) for(OnEvent policy:on) {
                if(event.equals(policy.event)) {
                    if(defaultValue!=null && event.equals("empty")) throw new IllegalArgumentException("Duplicate ON EMPTY policy");
                    sql.append(policy.reject ? " ERROR" : policy.defaultValue==null ? " NULL" : " DEFAULT "+jsonDefault(policy.defaultValue))
                            .append(" ON ").append(event.toUpperCase(Locale.ROOT));
                }
            }
            return sql.toString();
        }

        /** JSON 默认值采用带字符集的十六进制字面量，避免引号及 SQL 模式影响。 */
        private static String jsonDefault(Object value) {
            return "_utf8mb4 "+MysqlLiteral.hex(MysqlLiteral.jsonScalar(value));
        }

        public static class OnEvent {
            protected JsonColumnBuilder jsonColumnBuilder;
            //ON ERROR 和 ON EMPTY
            private String event;
            private boolean reject;
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

        /** 生成从一开始的行号列。 */
        public JsonColumnBuilder ordinality() { jsonColumn.ordinality=true;return this; }
        /** 缺失值或转换错误选择明确报错，而非静默返回 NULL。 */
        public JsonColumnBuilder errorOnEmpty() {
            JsonColumn.OnEvent event=new JsonColumn.OnEvent(this);event.event="empty";event.reject=true;
            jsonColumn.on.add(event);return this;
        }
        public JsonColumnBuilder errorOnError() {
            JsonColumn.OnEvent event=new JsonColumn.OnEvent(this);event.event="error";event.reject=true;
            jsonColumn.on.add(event);return this;
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
