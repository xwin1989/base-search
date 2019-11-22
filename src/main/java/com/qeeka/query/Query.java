package com.qeeka.query;

import com.qeeka.domain.Sort;
import com.qeeka.http.BaseRequest;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by neal.xu on 2019/10/30.
 */
public class Query {
    public final static String SPECIAL_QUERY_SQL_KEY = "_$SPECIAL_QUERY_SQL";
    /**
     * select column
     */
    private CharSequence[] selects;
    /**
     * group by
     */
    private Group groupBy;
    /**
     * where criteria
     */
    private final Criteria criteria;
    /**
     * join other
     */
    private List<Join> joinChain;
    /**
     * sort column
     */
    private Sort sort;
    /**
     * page index and set default
     */
    private Integer index;

    /**
     * page size and set default
     */
    private Integer size;

    /**
     * need total record
     */
    private boolean needCount = false;

    /**
     * record need distinct
     */
    private boolean needDistinct = false;


    public Query(Criteria criteria) {
        this.criteria = criteria;
    }

    public static Query query() {
        return new Query(null);
    }

    public static Query query(Criteria criteria) {
        return new Query(criteria);
    }
//--------------------------- join operator -------------------------

    public Query join(Join join) {
        if (this.joinChain == null) this.joinChain = new LinkedList<>();
        this.joinChain.add(join);
        return this;
    }

    public Query group(Group groupBy) {
        this.groupBy = groupBy;
        return this;
    }

//--------------------------- with operator -------------------------

    public Query with(Sort sort) {
        this.sort = sort;
        return this;
    }

    public Query with(Integer pageIndex, Integer pageSize) {
        this.index = pageIndex;
        this.size = pageSize;
        return this;
    }

    public Query with(BaseRequest request) {
        with(request.getPageIndex(), request.getPageSize());
        return this;
    }

//--------------------------- set operator -------------------------

    public Query count() {
        this.needCount = true;
        return this;
    }

    public Query distinct() {
        this.needDistinct = true;
        return this;
    }

    public Query index(Integer index) {
        this.index = index;
        return this;
    }

    public Query size(Integer size) {
        this.size = size;
        return this;
    }

    public Query selects(CharSequence... selectFields) {
        this.selects = selectFields;
        return this;
    }

//--------------------------- get operator -------------------------

    public Criteria getCriteria() {
        return criteria;
    }

    public Sort getSort() {
        return sort;
    }


}
