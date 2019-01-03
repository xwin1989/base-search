package com.qeeka.domain;

import com.qeeka.enums.QueryOperate;

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


    public UpdateGroup(String columnName, Object value) {
        this.updateNodeList.add(new UpdateNode(columnName, value));
    }


    public UpdateGroup(String columnName) {
        this.updateNodeList.add(new UpdateNode(columnName, null));
    }

    public UpdateGroup set(String columnName) {
        this.updateNodeList.add(new UpdateNode(columnName, null));
        return this;
    }

    public UpdateGroup set(String columnName, Object value) {
        this.updateNodeList.add(new UpdateNode(columnName, value));
        return this;
    }

    public UpdateGroup where(String columnName, Object value) {
        return where(new QueryGroup(columnName, value));
    }

    public UpdateGroup where(String columnName, QueryOperate operate) {
        return where(new QueryGroup(columnName, operate));
    }

    public UpdateGroup where(String columnName, Object value, QueryOperate operate) {
        return where(new QueryGroup(columnName, value, operate));
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
