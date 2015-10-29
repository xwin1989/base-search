package com.qeeka.domain.elastic.group;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by neal.xu on 2015/10/20
 */
public class ESQueryGroup {
    @XmlElement(name = "filtered")
    private ESFilteredGroup filtered;

    public ESFilteredGroup getFiltered() {
        return filtered;
    }

    public void setFiltered(ESFilteredGroup filtered) {
        this.filtered = filtered;
    }
}
