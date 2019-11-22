package com.qeeka.enums;

/**
 * Created by neal.xu on 2018/7/29.
 */
public enum QueryLinkOperate {
    AND(" AND "), OR(" OR "),
    INNER_JOIN(" INNER JOIN "), CROSS_JOIN(" CROSS JOIN "),
    LEFT_JOIN(" LEFT JOIN "), LEFT_OUT_JOIN(" LEFT OUTER JOIN "),
    RIGHT_JOIN(" RIGHT JOIN "), RIGHT_OUT_JOIN(" RIGHT OUTER JOIN ");

    private String value;

    QueryLinkOperate(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
