package com.qeeka.query;

import com.qeeka.enums.QueryLinkOperate;

/**
 * Created by neal.xu on 2019/11/19.
 */
public class Join {
    private Criteria criteria;
    private QueryLinkOperate linkOperate;
    private String name;
    private String alias;

    public Join(String name, String alias, QueryLinkOperate linkOperate) {
        this.name = name;
        this.alias = alias;
        this.linkOperate = linkOperate;
    }

    public static Join inner(String name, String alias) {
        return new Join(name, alias, QueryLinkOperate.INNER_JOIN);
    }

    public static Join left(String name, String alias) {
        return new Join(name, alias, QueryLinkOperate.LEFT_JOIN);
    }

    public static Join leftOut(String name, String alias) {
        return new Join(name, alias, QueryLinkOperate.LEFT_OUT_JOIN);
    }

    public static Join right(String name, String alias) {
        return new Join(name, alias, QueryLinkOperate.RIGHT_JOIN);
    }

    public static Join rightOut(String name, String alias) {
        return new Join(name, alias, QueryLinkOperate.RIGHT_OUT_JOIN);
    }

    public static Join cross(String name, String alias) {
        return new Join(name, alias, QueryLinkOperate.CROSS_JOIN);
    }

    public Join on(Criteria criteria) {
        this.criteria = criteria;
        return this;
    }
}
