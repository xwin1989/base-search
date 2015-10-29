package com.qeeka.domain.elastic.node;

import com.qeeka.domain.elastic.ESTree;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by neal.xu on 2015/10/22
 */
public class ESBoolNode implements ESTree {

    @XmlElementWrapper(name = "must")
    @XmlElement
    private List<ESTree> must;

    @XmlElementWrapper(name = "must_not")
    @XmlElement
    private List<ESTree> mustNot;

    @XmlElementWrapper(name = "should")
    @XmlElement
    private List<ESTree> should;

    @XmlElement(name = "minimum_should_match")
    private Integer minimumMatch;

    public List<ESTree> getShould() {
        return should;
    }

    public void setShould(List<ESTree> should) {
        this.should = should;
    }

    public List<ESTree> getMust() {
        return must;
    }

    public void setMust(List<ESTree> must) {
        this.must = must;
    }

    public List<ESTree> getMustNot() {
        return mustNot;
    }

    public void setMustNot(List<ESTree> mustNot) {
        this.mustNot = mustNot;
    }

    public Integer getMinimumMatch() {
        return minimumMatch;
    }

    public void setMinimumMatch(Integer minimumMatch) {
        this.minimumMatch = minimumMatch;
    }

    public void addMust(ESTree node) {
        if (this.must == null) must = new ArrayList<>();
        this.must.add(node);
    }

    public void addMustNot(ESTree node) {
        if (this.mustNot == null) mustNot = new ArrayList<>();
        this.mustNot.add(node);
    }

    public void addShould(ESTree node) {
        if (this.should == null) should = new ArrayList<>();
        this.should.add(node);
    }
}
