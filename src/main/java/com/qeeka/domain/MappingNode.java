package com.qeeka.domain;

import com.qeeka.enums.QueryLinkOperate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neal on 2016/11/28.
 */
public class MappingNode {
    private QueryLinkOperate linkOperate;
    private String name;
    private String alias;
    private List<QueryNode> linkMapping = new ArrayList<>();

    public MappingNode(String name, String alias, QueryLinkOperate linkOperate) {
        this.linkOperate = linkOperate;
        this.name = name;
        this.alias = alias;
    }

    public QueryLinkOperate getLinkOperate() {
        return linkOperate;
    }

    public void setLinkOperate(QueryLinkOperate linkOperate) {
        this.linkOperate = linkOperate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<QueryNode> getLinkMapping() {
        return linkMapping;
    }

    public void setLinkMapping(List<QueryNode> linkMapping) {
        this.linkMapping = linkMapping;
    }
}
