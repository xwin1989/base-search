package com.qeeka.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by neal.xu on 8/1 0001.
 */
public class QueryModel {
    private String tableStatement;
    private String conditionStatement;
    private String orderStatement;
    private String selectStatement;
    private String countStatement;
    private String groupStatement;
    private String pageableStatement;
    private Map<String, Object> parameters = new HashMap<>();

    public String getConditionStatement() {
        return conditionStatement;
    }

    public void setConditionStatement(String conditionStatement) {
        this.conditionStatement = conditionStatement;
    }

    public String getOrderStatement() {
        return orderStatement;
    }

    public void setOrderStatement(String orderStatement) {
        this.orderStatement = orderStatement;
    }

    public String getSelectStatement() {
        return selectStatement;
    }

    public void setSelectStatement(String selectStatement) {
        this.selectStatement = selectStatement;
    }

    public String getCountStatement() {
        return countStatement;
    }

    public void setCountStatement(String countStatement) {
        this.countStatement = countStatement;
    }

    public String getTableStatement() {
        return tableStatement;
    }

    public void setTableStatement(String tableStatement) {
        this.tableStatement = tableStatement;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public String getGroupStatement() {
        return groupStatement;
    }

    public void setGroupStatement(String groupStatement) {
        this.groupStatement = groupStatement;
    }

    public String getPageableStatement() {
        return pageableStatement;
    }

    public void setPageableStatement(String pageableStatement) {
        this.pageableStatement = pageableStatement;
    }
}
