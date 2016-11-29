package com.qeeka.operate;

/**
 * Created by neal.xu on 7/29 0029.
 */
public enum QueryLinkOperate {
    AND(" AND "), OR(" OR "),
    INNER_JOIN(" INNER JOIN "), INNER_JOIN_FETCH(" INNER JOIN ", true),
    LEFT_JOIN(" LEFT JOIN "), LEFT_JOIN_FETCH(" LEFT JOIN ", true),
    LEFT_OUT_JOIN(" LEFT OUT JOIN "), LEFT_OUT_JOIN_FETCH(" LEFT OUT JOIN ", true),
    RIGHT_JOIN(" RIGHT JOIN "), RIGHT_OUT_JOIN(" RIGHT OUT JOIN "),
    CROSS_JOIN(" CROSS JOIN "), FULL_JOIN(" FULL JOIN ");

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
