package com.pengwz.dynamic.sql2.table;

import com.pengwz.dynamic.sql2.enums.GenerationType;

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
