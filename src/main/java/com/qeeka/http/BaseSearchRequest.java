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
    protected Integer pageIndex = 0;
    @XmlElement(name = "page_size")
    protected Integer pageSize = 10;

    public BaseSearchRequest(Integer pageIndex, Integer pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
