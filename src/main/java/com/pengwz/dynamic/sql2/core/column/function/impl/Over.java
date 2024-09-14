package com.pengwz.dynamic.sql2.core.column.function.impl;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.enums.SortOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * 窗口函数 over部分
 */
public class Over {
    private String partitionByClause = "";
    private List<String> orderByColumns = new ArrayList<>();
    private String frameSpecification = "";

    private Over() {
    }

    public static OverBuilder builder() {
        return new OverBuilder();
    }

    public String getPartitionByClause() {
        return partitionByClause;
    }

    public List<String> getOrderByColumns() {
        return orderByColumns;
    }

    public String getFrameSpecification() {
        return frameSpecification;
    }

    public static class OverBuilder {
        private Over over = new Over();

        private OverBuilder() {
        }

        public Over build() {
            return over;
        }

        public <T, F> OverBuilder orderBy(Fn<T, F> fn) {
            return orderBy(fn, SortOrder.ASC); // Default to ascending order
        }

        public <T, F> OverBuilder orderBy(Fn<T, F> fn, SortOrder sortOrder) {
            // Convert fn to column name or expression
            over.orderByColumns.add(fn.toString() + " " + sortOrder.name());
            return this;
        }

        public OverBuilder partitionBy(Fn<?, ?> fn) {
            // Convert fn to column name or expression
            over.partitionByClause = "PARTITION BY " + fn.toString();
            return this;
        }

        public OverBuilder frameSpecification(String frameSpec) {
            over.frameSpecification = frameSpec;
            return this;
        }

        public OverBuilder unboundedPrecedingToCurrentRow() {
            over.frameSpecification = "ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW";
            return this;
        }

        public OverBuilder currentRowToUnboundedFollowing() {
            over.frameSpecification = "ROWS BETWEEN CURRENT ROW AND UNBOUNDED FOLLOWING";
            return this;
        }

        public OverBuilder nPrecedingToCurrentRow(int n) {
            over.frameSpecification = "ROWS BETWEEN " + n + " PRECEDING AND CURRENT ROW";
            return this;
        }

        public OverBuilder nFollowingToCurrentRow(int n) {
            over.frameSpecification = "ROWS BETWEEN CURRENT ROW AND " + n + " FOLLOWING";
            return this;
        }

        public OverBuilder nPrecedingToNFollowing(int nPreceding, int nFollowing) {
            over.frameSpecification = "ROWS BETWEEN " + nPreceding + " PRECEDING AND " + nFollowing + " FOLLOWING";
            return this;
        }
    }
}
