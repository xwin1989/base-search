package com.qeeka.domain;

import com.qeeka.operate.QueryLinkOperate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neal on 2016/11/28.
 */
public class MappingNode {
    private QueryLinkOperate linkOperate;
    private String entityName;
    private String entityAlias;
    private List<QueryNode> linkMapping = new ArrayList<>();

    public MappingNode(String entityName, String entityAlias, QueryLinkOperate linkOperate) {
        this.linkOperate = linkOperate;
        this.entityName = entityName;
        this.entityAlias = entityAlias;
    }

    public QueryLinkOperate getLinkOperate() {
        return linkOperate;
    }

    public void setLinkOperate(QueryLinkOperate linkOperate) {
        this.linkOperate = linkOperate;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityAlias() {
        return entityAlias;
    }

    public void setEntityAlias(String entityAlias) {
        this.entityAlias = entityAlias;
    }

    public List<QueryNode> getLinkMapping() {
        return linkMapping;
    }

    public void setLinkMapping(List<QueryNode> linkMapping) {
        this.linkMapping = linkMapping;
    }
}
