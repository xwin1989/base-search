package com.qeeka.domain.elastic.group;

import com.qeeka.domain.elastic.custom.ESGroupByNode;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by kimi.lai on 11/25/2015.
 */
public class ESAggsGroup {
    @XmlElement(name = "group_by")
    private ESGroupByNode groupByNode;

    public ESGroupByNode getGroupByNode() {
        return groupByNode;
    }

    public void setGroupByNode(ESGroupByNode groupByNode) {
        this.groupByNode = groupByNode;
    }
}