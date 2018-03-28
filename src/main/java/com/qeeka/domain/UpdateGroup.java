package com.qeeka.domain;

import com.qeeka.operate.UpdateOperate;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by neal.xu on 2017/08/11.
 */
public class UpdateGroup {
    /**
     * update handle node list
     */
    private final List<UpdateNode> updateNodeList = new LinkedList<>();
    private QueryGroup queryGroup;

    public UpdateGroup() {
    }

    public UpdateGroup(String columnName, Object value) {
        this.updateNodeList.add(new UpdateNode(columnName, value));
    }

    public UpdateGroup(String columnName, Object value, UpdateOperate operate) {
        this.updateNodeList.add(new UpdateNode(columnName, value, operate));
    }

    public UpdateGroup set(String columnName, Object value) {
        this.updateNodeList.add(new UpdateNode(columnName, value));
        return this;
    }

    public UpdateGroup set(String columnName, Object value, UpdateOperate operate) {
        this.updateNodeList.add(new UpdateNode(columnName, value, operate));
        return this;
    }

    public UpdateGroup where(QueryGroup group) {
        this.queryGroup = group;
        return this;
    }

    public List<UpdateNode> getUpdateNodeList() {
        return updateNodeList;
    }

    public QueryGroup getQueryGroup() {
        return queryGroup;
    }
}
