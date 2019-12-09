package com.qeeka.domain;

import java.util.List;

/**
 * Created by Neal on 2019/08/09.
 */
public class QueryResponse<T> {
    private List<T> records;
    private Long total;

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
