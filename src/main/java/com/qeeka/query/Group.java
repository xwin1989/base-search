package com.qeeka.query;

/**
 * Created by neal.xu on 2019/11/20.
 */
public class Group {

    private final CharSequence[] selects;
    private Criteria havingCriteria;

    public Group(CharSequence[] selects) {
        this.selects = selects;
    }

    public static Group by(CharSequence... selects) {
        return new Group(selects);
    }

    public Group having(Criteria criteria) {
        this.havingCriteria = criteria;
        return this;
    }

    public CharSequence[] getSelects() {
        return selects;
    }

    public Criteria getHavingCriteria() {
        return havingCriteria;
    }
}
