package com.qeeka.domain;

import com.qeeka.operate.QueryLinkOperate;

public class QueryOperateNode implements QueryHandle {

    private QueryLinkOperate queryLinkOperate = QueryLinkOperate.AND;

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