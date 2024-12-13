package com.dynamic.sql.table;


import com.dynamic.sql.enums.GenerationType;

public class GeneratedStrategy {
    GenerationType strategy;
    String sequenceName;

    public GenerationType getStrategy() {
        return strategy;
    }

    public void setStrategy(GenerationType strategy) {
        this.strategy = strategy;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }
}
