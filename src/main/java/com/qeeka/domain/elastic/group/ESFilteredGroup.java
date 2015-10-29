package com.qeeka.domain.elastic.group;

import com.qeeka.domain.elastic.node.ESFilterNode;
import com.qeeka.domain.elastic.node.ESQueryNode;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by neal.xu on 2015/10/20
 */
public class ESFilteredGroup {
    @XmlElement(name = "query")
    private ESQueryNode query;
    @XmlElement(name = "filter")
    private ESFilterNode filter;

    public ESQueryNode getQuery() {
        return query;
    }

    public void setQuery(ESQueryNode query) {
        this.query = query;
    }

    public ESFilterNode getFilter() {
        return filter;
    }

    public void setFilter(ESFilterNode filter) {
        this.filter = filter;
    }
}
