package com.qeeka.domain;

import com.qeeka.operate.UpdateOperate;

/**
 * Created by neal.xu on 2017/08/11.
 */
public class UpdateNode {
    private String columnName;
    private Object value;
    private UpdateOperate updateOperate = UpdateOperate.EQUALS;

    public UpdateNode() {
    }

    public UpdateNode(String columnName, Object value) {
        this.columnName = columnName;
        this.value = value;
    }

    public UpdateNode(String columnName, Object value, UpdateOperate updateOperate) {
        this.columnName = columnName;
        this.value = value;
        this.updateOperate = updateOperate;
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

    public UpdateOperate getUpdateOperate() {
        return updateOperate;
    }

    public void setUpdateOperate(UpdateOperate updateOperate) {
        this.updateOperate = updateOperate;
    }
}
