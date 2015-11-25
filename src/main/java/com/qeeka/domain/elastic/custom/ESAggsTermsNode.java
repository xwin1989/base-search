package com.qeeka.domain.elastic.custom;

import com.qeeka.domain.elastic.ESTree;
import com.qeeka.domain.elastic.group.ESAggsGroup;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Map;

/**
 * Created by kimi.lai on 11/25/2015.
 */
public class ESAggsTermsNode implements ESTree {
    private static final String FIELD = "field";
    @XmlElement(name = "aggs")
    private ESAggsGroup aggsGroup;

    @XmlElement(name = "terms")
    private Map<String, Object> termsNode;

    public ESAggsTermsNode(Object value) {
        Map<String, Object> node = Collections.singletonMap(FIELD, value);
        this.termsNode = node;
    }

    public ESAggsTermsNode addAggs(ESAggsGroup aggs) {
        this.aggsGroup = aggs;
        return this;
    }

    public ESAggsGroup getAggsGroup() {
        return aggsGroup;
    }

    public void setAggsGroup(ESAggsGroup aggsGroup) {
        this.aggsGroup = aggsGroup;
    }

    public Map<String, Object> getTermsNode() {
        return termsNode;
    }

    public void setTermsNode(Map<String, Object> termsNode) {
        this.termsNode = termsNode;
    }
}
