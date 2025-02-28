package com.dynamic.sql.context.properties;

public class SqlLogConfig {
    private Boolean suppressSqlLog;
    private Boolean forceSqlLog;
    private final OutputParam outputParam = new OutputParam();

    public boolean isSuppressSqlLog() {
        return suppressSqlLog != null && suppressSqlLog;
    }

    public SqlLogConfig setSuppressSqlLog(Boolean suppressSqlLog) {
        this.suppressSqlLog = suppressSqlLog;
        return this;
    }

    public boolean isForceSqlLog() {
        return forceSqlLog != null && forceSqlLog;
    }

    public SqlLogConfig setForceSqlLog(Boolean forceSqlLog) {
        this.forceSqlLog = forceSqlLog;
        return this;
    }

    public OutputParam getOutputParam() {
        return outputParam;
    }

    public static class OutputParam {
        private boolean outputPreparing = true;
        private boolean outputParameters = true;
        private boolean outputCount = true;
        private boolean outputTotal = true;
        private boolean outputReturned = true;
        private boolean outputAffectedRows = true;

        public boolean isOutputPreparing() {
            return outputPreparing;
        }

        public void setOutputPreparing(boolean outputPreparing) {
            this.outputPreparing = outputPreparing;
        }

        public boolean isOutputParameters() {
            return outputParameters;
        }

        public void setOutputParameters(boolean outputParameters) {
            this.outputParameters = outputParameters;
        }

        public boolean isOutputCount() {
            return outputCount;
        }

        public void setOutputCount(boolean outputCount) {
            this.outputCount = outputCount;
        }

        public boolean isOutputTotal() {
            return outputTotal;
        }

        public void setOutputTotal(boolean outputTotal) {
            this.outputTotal = outputTotal;
        }

        public boolean isOutputReturned() {
            return outputReturned;
        }

        public void setOutputReturned(boolean outputReturned) {
            this.outputReturned = outputReturned;
        }

        public boolean isOutputAffectedRows() {
            return outputAffectedRows;
        }

        public void setOutputAffectedRows(boolean outputAffectedRows) {
            this.outputAffectedRows = outputAffectedRows;
        }
    }
}
