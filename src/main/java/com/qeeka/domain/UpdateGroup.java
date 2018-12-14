package com.qeeka.domain;

import com.qeeka.enums.QueryOperate;
import com.qeeka.enums.UpdateOperate;

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
    private QueryGroup queryGroup = new QueryGroup();


    public UpdateGroup(String columnName, Object value) {
        this.updateNodeList.add(new UpdateNode(columnName, value, UpdateOperate.EQUALS));
    }

    public UpdateGroup(String columnName, Object value, UpdateOperate updateOperate) {
        this.updateNodeList.add(new UpdateNode(columnName, value, updateOperate));
    }


    public UpdateGroup set(String columnName, Object value) {
        this.updateNodeList.add(new UpdateNode(columnName, value, UpdateOperate.EQUALS));
        return this;
    }

    public UpdateGroup set(String columnName, Object value, UpdateOperate updateOperate) {
        this.updateNodeList.add(new UpdateNode(columnName, value, updateOperate));
        return this;
    }

    public UpdateGroup where(String columnName, Object value) {
        return where(columnName, value, QueryOperate.EQUALS);
    }

    public UpdateGroup where(String columnName, QueryOperate queryOperate) {
        return where(columnName, null, queryOperate);
    }

    public UpdateGroup where(String columnName, Object value, QueryOperate queryOperate) {
        return where(new QueryGroup(columnName, value, queryOperate));
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
