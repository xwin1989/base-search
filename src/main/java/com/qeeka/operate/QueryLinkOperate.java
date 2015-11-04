package com.qeeka.operate;

/**
 * Created by neal.xu on 7/29 0029.
 */
public enum QueryLinkOperate {
    AND(" AND "), OR(" OR "),
    INNER_JOIN(" INNER JOIN "), INNER_JOIN_FETCH(" INNER JOIN ", true),
    LEFT_JOIN(" LEFT JOIN "), LEFT_JOIN_FETCH(" LEFT JOIN ", true),
    CROSS_JOIN(" CROSS JOIN ");
    private String value;
    private boolean needFetch = false;

    QueryLinkOperate(String value) {
        this.value = value;
    }

    QueryLinkOperate(String value, boolean needFetch) {
        this.value = value;
        this.needFetch = needFetch;
    }

    public String getValue() {
        return value;
    }

    public boolean isNeedFetch() {
        return needFetch;
    }
}
