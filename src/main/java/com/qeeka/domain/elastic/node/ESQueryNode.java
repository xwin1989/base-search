package com.qeeka.domain.elastic.node;

import com.qeeka.domain.elastic.ESTree;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;

/**
 * Created by neal.xu on 2015/10/20
 */
public class ESQueryNode {
    @XmlElement(name = "bool")
    private ESBoolNode boolNode;

    public ESBoolNode getBoolNode() {
        return boolNode;
    }

    public void setBoolNode(ESBoolNode boolNode) {
        this.boolNode = boolNode;
    }

    public ESQueryNode addMust(ESTree node) {
        if (boolNode == null) boolNode = new ESBoolNode();
        if (this.boolNode.getMust() == null) this.boolNode.setMust(new ArrayList<ESTree>());
        this.boolNode.getMust().add(node);
        return this;
    }

    public ESQueryNode addMustNot(ESTree node) {
        if (boolNode == null) boolNode = new ESBoolNode();
        if (this.boolNode.getMustNot() == null) this.boolNode.setMustNot(new ArrayList<ESTree>());
        this.boolNode.getMustNot().add(node);
        return this;
    }

    public ESQueryNode addShould(ESTree node) {
        if (boolNode == null) boolNode = new ESBoolNode();
        if (this.boolNode.getShould() == null) this.boolNode.setShould(new ArrayList<ESTree>());
        this.boolNode.getShould().add(node);
        return this;
    }
}
