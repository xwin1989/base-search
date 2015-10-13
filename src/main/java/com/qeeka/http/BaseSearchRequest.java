package com.qeeka.http;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Neal on 10/12 0012.
 */
@XmlRootElement(name = "base_search_request")
@XmlAccessorType(XmlAccessType.FIELD)
public class BaseSearchRequest {
    @XmlElement(name = "page_index")
    protected int pageIndex;
    @XmlElement(name = "page_size")
    protected int pageSize;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
