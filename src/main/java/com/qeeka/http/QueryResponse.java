package com.qeeka.http;

import com.qeeka.domain.MapHandle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Neal on 8/9 0009.
 */
public class QueryResponse<T> {
    private T entity;
    private List<T> records;
    private Long totalRecords;
    private Integer pageIndex;
    private Integer pageSize;

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
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

    public <X extends BaseSearchResponse<T>> X assignmentToResponse(X response) {
        if (response != null) {
            response.setEntity(this.entity);
            response.setRecordList(this.records);
            response.setTotalRecords(this.totalRecords);
            response.setPageIndex(this.pageIndex);
            response.setPageSize(this.pageSize);
            return response;
        }
        return null;
    }

    public Map<Object, T> getRecordsMap() {
        Map<Object, T> recordMap = new HashMap<>();
        if (this.records != null && !this.records.isEmpty() && this.records.get(0) instanceof MapHandle) {
            for (T result : this.records) {
                recordMap.put(((MapHandle) result).getPrimaryKey(), result);
            }
        }
        return recordMap;
    }

    public List<Object> getRecordsKey() {
        List<Object> keys = new ArrayList<>();
        if (this.records != null && !this.records.isEmpty() && this.records.get(0) instanceof MapHandle) {
            for (T result : this.records) {
                keys.add(((MapHandle) result).getPrimaryKey());
            }
        }
        return keys;
    }

    public Map<Object, List<T>> getMultiRecordMap() {
        Map<Object, List<T>> recordMap = new HashMap<>();
        if (this.records != null && !this.records.isEmpty() && this.records.get(0) instanceof MapHandle) {
            for (T result : this.records) {
                Object k = ((MapHandle) result).getPrimaryKey();
                List<T> v = recordMap.get(k);
                if (v == null) {
                    v = new ArrayList<>();
                    v.add(result);
                    recordMap.put(k, v);
                } else {
                    v.add(result);
                }
            }
        }
        return recordMap;
    }
}
