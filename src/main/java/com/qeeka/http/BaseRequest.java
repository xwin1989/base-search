package com.qeeka.http;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Neal on 2018/10/12.
 */
public class BaseRequest {
    @JsonProperty("page_index")
    protected Integer pageIndex = 0;
    @JsonProperty("page_size")
    protected Integer pageSize = 10;

    public BaseRequest() {
    }

    public BaseRequest(Integer pageIndex, Integer pageSize) {
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

    public Integer getOffset() {
        if (this.pageIndex == null || this.pageSize == null) {
            return 0;
        }
        return this.pageIndex * this.getPageSize();
    }
}
