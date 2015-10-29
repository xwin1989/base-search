package com.qeeka.domain.elastic.custom;

import com.qeeka.domain.elastic.ESTree;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Map;

/**
 * Created by neal.xu on 2015/10/22
 */
public class ESExistsNode implements ESTree {
    @XmlElement(name = "exists")
    private Map<String, String> exists;

    public ESExistsNode(String fieldName) {
        Map<String, String> node = Collections.singletonMap("field", fieldName);
        this.exists = node;
    }

    public Map<String, String> getExists() {
        return exists;
    }

    public void setExists(Map<String, String> exists) {
        this.exists = exists;
    }
}
