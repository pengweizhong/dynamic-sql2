package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.windows.Over;

public class ColumnInfo {
    //别名
    private String alias;
    //列函数
    private ColumFunction columFunction;
    //滑窗函数
    private Over over;

    public static Builder builder() {
        return new Builder();
    }

    public String getAlias() {
        return alias;
    }

    public ColumFunction getColumFunction() {
        return columFunction;
    }

    public Over getOver() {
        return over;
    }

    public static class Builder {
        private ColumnInfo columnInfo;

        public Builder() {
            columnInfo = new ColumnInfo();
        }

        public Builder alias(String alias) {
            columnInfo.alias = alias;
            return this;
        }

        public Builder columFunction(ColumFunction columFunction) {
            columnInfo.columFunction = columFunction;
            return this;
        }

        public Builder over(Over over) {
            columnInfo.over = over;
            return this;
        }

        public ColumnInfo build() {
            return columnInfo;
        }
    }
}
