package com.qeeka.domain;

import com.qeeka.enums.GenerationType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by neal.xu on 2018/12/12.
 */
public class EntityInfo {
    private Class clazz;
    //column mapping , name -> column
    private Map<String, String> columnMap = new LinkedHashMap<>();
    private String idColumn;
    private GenerationType strategy;
    private String tableName;

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getIdColumn() {
        return idColumn;
    }

    public void setIdColumn(String idColumn) {
        this.idColumn = idColumn;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public GenerationType getStrategy() {
        return strategy;
    }

    public void setStrategy(GenerationType strategy) {
        this.strategy = strategy;
    }

    public Map<String, String> getColumnMap() {
        return columnMap;
    }

    public void setColumnMap(Map<String, String> columnMap) {
        this.columnMap = columnMap;
    }
}
