package com.qeeka.domain;

import java.util.List;

/**
 * Created by Neal on 8/9 0009.
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

    public Integer getTotal() {
        if (total == null) {
            return null;
        }
        return Integer.valueOf(String.valueOf(total));
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getLongTotal() {
        return total;
    }
}
