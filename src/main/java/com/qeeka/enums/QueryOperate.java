package com.qeeka.enums;

/**
 * Created by neal.xu on 2018/07/29.
 */
public enum QueryOperate {
    EQUALS(" = "), NO_EQUALS(" <> "),
    IS_NULL(""), IS_NOT_NULL(""),
    COLUMN_EQUALS(" = "), COLUMN_NO_EQUALS(" <> "),
    LIKE(" LIKE "), NOT_LIKE(" NOT LIKE "), CONTAIN(" LIKE "), NOT_CONTAIN(" NOT LIKE "),
    LESS_THAN(" < "), LESS_THAN_EQUALS(" <= "),
    GREAT_THAN(" > "), GREAT_THAN_EQUALS(" >= "),
    IN(" IN "), NOT_IN(" NOT IN "), SUB_QUERY(" "),
    EXISTS(" EXISTS"), NO_EXISTS(" NOT EXISTS ");

    private String value;

    QueryOperate(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
