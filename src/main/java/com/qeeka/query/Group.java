package com.qeeka.query;

import java.util.Arrays;
import java.util.List;

/**
 * Created by neal.xu on 2019/11/20.
 */
public class Group {

    private final List<CharSequence> selects;
    private Criteria havingCriteria;

    public Group(List<CharSequence> selects) {
        this.selects = selects;
    }

    public static Group by(List<CharSequence> selects) {
        return new Group(selects);
    }

    public static Group by(CharSequence... selects) {
        return new Group(Arrays.asList(selects));
    }

    public Group having(Criteria criteria) {
        this.havingCriteria = criteria;
        return this;
    }

    public List<CharSequence> getSelects() {
        return selects;
    }

    public Criteria getHavingCriteria() {
        return havingCriteria;
    }
}
