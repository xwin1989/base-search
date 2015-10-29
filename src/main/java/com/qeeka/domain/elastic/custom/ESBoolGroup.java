package com.qeeka.domain.elastic.custom;

import com.qeeka.domain.elastic.ESTree;
import com.qeeka.domain.elastic.node.ESBoolNode;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;

/**
 * Created by neal.xu on 2015/10/20
 */
public class ESBoolGroup implements ESTree {
    @XmlElement(name = "bool")
    private ESBoolNode boolNode;

    public ESBoolNode getBoolNode() {
        return boolNode;
    }

    public void setBoolNode(ESBoolNode boolNode) {
        this.boolNode = boolNode;
    }

    public void addMust(ESTree node) {
        if (boolNode == null) boolNode = new ESBoolNode();
        if (boolNode.getMust() == null) boolNode.setMust(new ArrayList());
        this.boolNode.getMust().add(node);
    }

    public void addMustNot(ESTree node) {
        if (boolNode == null) boolNode = new ESBoolNode();
        if (boolNode.getMustNot() == null) boolNode.setMustNot(new ArrayList());
        this.boolNode.getMustNot().add(node);
    }

    public void addShould(ESTree node) {
        if (boolNode == null) boolNode = new ESBoolNode();
        if (boolNode.getShould() == null) boolNode.setShould(new ArrayList());
        this.boolNode.getShould().add(node);
    }
}
