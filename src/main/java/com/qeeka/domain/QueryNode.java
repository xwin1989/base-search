package com.qeeka.domain;

import com.qeeka.enums.QueryOperate;

/**
 * Created by neal.xu on 7/31 0031.
 */
public class QueryNode implements QueryHandle {
    private String columnName;
    private Object value;
    private QueryOperate queryOperate;

    public QueryNode(String columnName, Object value, QueryOperate queryOperate) {
        this.value = value;
        this.columnName = columnName;
        this.queryOperate = queryOperate;
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

    public QueryOperate getQueryOperate() {
        return queryOperate;
    }

    public void setQueryOperate(QueryOperate queryOperate) {
        this.queryOperate = queryOperate;
    }
}
