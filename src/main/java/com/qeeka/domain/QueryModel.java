package com.qeeka.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by neal.xu on 8/1 0001.
 */
public class QueryModel {
    private String conditionStatement;
    private String orderStatement;
    private String selectStatement;
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

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
