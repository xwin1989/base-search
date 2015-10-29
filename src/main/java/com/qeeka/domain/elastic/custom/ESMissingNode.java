package com.qeeka.domain.elastic.custom;

import com.qeeka.domain.elastic.ESTree;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Map;

/**
 * Created by neal.xu on 2015/10/22
 */
public class ESMissingNode implements ESTree {
    @XmlElement(name = "missing")
    private Map<String, String> missing;

    public ESMissingNode(String fieldName) {
        Map<String, String> node = Collections.singletonMap("field", fieldName);
        this.missing = node;
    }

    public Map<String, String> getMissing() {
        return missing;
    }

    public void setMissing(Map<String, String> missing) {
        this.missing = missing;
    }
}
