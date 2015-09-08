package com.qeeka.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by neal.xu on 8/1 0001.
 */
public class QueryModel {
    private String statement;
    private String orderStatement;
    private Map<String, Object> parameters = new HashMap<>();

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getOrderStatement() {
        return orderStatement;
    }

    public void setOrderStatement(String orderStatement) {
        this.orderStatement = orderStatement;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

}
