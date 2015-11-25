package com.qeeka.domain.elastic.group;

import com.qeeka.domain.elastic.ESTree;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by kimi.lai on 11/25/2015.
 */
public class ESAggsGroup {
    @XmlElement(name = "group_by")
    private ESTree terms;

    public ESTree getTerms() {
        return terms;
    }

    public void setTerms(ESTree terms) {
        this.terms = terms;
    }

    public ESAggsGroup addTerms(ESTree node) {
        this.terms = node;
        return this;
    }
}