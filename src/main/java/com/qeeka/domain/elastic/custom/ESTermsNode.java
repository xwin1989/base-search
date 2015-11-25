package com.qeeka.domain.elastic.custom;

import com.qeeka.domain.elastic.ESTree;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Map;

/**
 * Created by kimi.lai on 11/25/2015.
 */
public class ESTermsNode implements ESTree {
    @XmlElement(name = "terms")
    private Map<String, Object> terms;

    public ESTermsNode(String key, Object value) {
        Map<String, Object> node = Collections.singletonMap(key, value);
        this.terms = node;
    }

    public Map<String, Object> getTerms() {
        return terms;
    }

    public void setTerms(Map<String, Object> terms) {
        this.terms = terms;
    }
}
