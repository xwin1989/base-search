package com.qeeka.domain.elastic.custom;

import com.qeeka.domain.elastic.ESTree;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.Map;

/**
 * Created by neal.xu on 2015/10/22
 */
public class ESWildcardNode implements ESTree {
    @XmlElement(name = "wildcard")
    private Map<String, Object> wildcard;

    public ESWildcardNode(String key, Object value) {
        Map<String, Object> node = Collections.singletonMap(key, value);
        this.wildcard = node;
    }

    public Map<String, Object> getWildcard() {
        return wildcard;
    }

    public void setWildcard(Map<String, Object> wildcard) {
        this.wildcard = wildcard;
    }
}
