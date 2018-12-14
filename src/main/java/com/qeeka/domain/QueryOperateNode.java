package com.qeeka.domain;

import com.qeeka.enums.QueryLinkOperate;

public class QueryOperateNode implements QueryHandle {

    private QueryLinkOperate queryLinkOperate;

    public QueryOperateNode(QueryLinkOperate queryLinkOperate) {
        this.queryLinkOperate = queryLinkOperate;
    }

    public QueryLinkOperate getQueryLinkOperate() {
        return queryLinkOperate;
    }

    public void setQueryLinkOperate(QueryLinkOperate queryLinkOperate) {
        this.queryLinkOperate = queryLinkOperate;
    }
}