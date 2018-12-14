package com.qeeka.domain;

public class QueryCombineNode implements QueryHandle {
    private String value;

    public QueryCombineNode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
