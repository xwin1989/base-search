package com.qeeka.domain.elastic.custom;

import com.qeeka.domain.elastic.node.ESAggregationNode;

import javax.xml.bind.annotation.XmlElement;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by kimi.lai on 11/25/2015.
 */
public class ESAggsTermsNode extends ESAggregationNode {
    @XmlElement(name = "terms")
    private Map<String, String> termsNode = new LinkedHashMap<>();

    public ESAggsTermsNode(String fieldName) {
        termsNode.put("field", fieldName);
    }

    public Map<String, String> getTermsNode() {
        return termsNode;
    }

    public void setTermsNode(Map<String, String> termsNode) {
        this.termsNode = termsNode;
    }
}
