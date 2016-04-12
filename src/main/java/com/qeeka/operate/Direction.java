package com.qeeka.operate;

public enum Direction {
    ASC("ASC"), DESC("DESC"), ASC_NULL("ASC"), DESC_NULL("DESC"), ASC_FIELD("FIELD"), DESC_FIELD("FIELD");
    private String value;

    Direction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}