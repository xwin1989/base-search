package com.qeeka.domain;

/**
 * Created by neal.xu on 2017/08/11.
 */
public class UpdateNode {
    private String columnName;
    private Object value;


    public UpdateNode(String columnName, Object value) {
        this.columnName = columnName;
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
