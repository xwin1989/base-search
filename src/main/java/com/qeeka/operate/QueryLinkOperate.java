package com.qeeka.operate;

/**
 * Created by neal.xu on 7/29 0029.
 */
public enum QueryLinkOperate {
    AND(" AND "), OR(" OR ");
    private String value;

    QueryLinkOperate(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
