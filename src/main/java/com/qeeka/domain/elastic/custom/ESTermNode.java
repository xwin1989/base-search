package com.qeeka.domain.elastic.custom;

import com.qeeka.domain.elastic.ESTree;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Map;

/**
 * Created by neal.xu on 2015/10/20
 */
public class ESTermNode implements ESTree {
    @XmlElement(name = "term")
    private Map<String, Object> term;

    public ESTermNode(String key, Object value) {
        Map<String, Object> node = Collections.singletonMap(key, value);
        this.term = node;
    }

    public Map<String, Object> getTerm() {
        return term;
    }

    public void setTerm(Map<String, Object> term) {
        this.term = term;
    }
}
