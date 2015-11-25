package com.qeeka.domain.elastic.node;

import javax.xml.bind.annotation.XmlElement;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by kimi.lai on 11/25/2015.
 */
public class ESAggregationNode {
    @XmlElement(name = "aggs")
    protected Map<String, ESAggregationNode> aggregationNode;

    public ESAggregationNode addAggregations(String columnName, ESAggregationNode aggregationNode) {
        if (this.aggregationNode == null) {
            this.aggregationNode = new LinkedHashMap<>();
        }
        this.aggregationNode.put(columnName, aggregationNode);
        return this;
    }

}
