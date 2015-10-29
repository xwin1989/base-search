package com.qeeka.http;

import com.qeeka.domain.MapHandle;

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
            response.setRecordMap(getObjectMap(this.getRecords()));
            response.setTotalRecords(this.totalRecords);
            response.setPageIndex(this.pageIndex);
            response.setPageSize(this.pageSize);
            return response;
        }
        return null;
    }

    private Map<Object, T> getObjectMap(List<T> ts) {
        Map<Object, T> recordMap = new HashMap<>();
        if (ts != null) {
            for (T t : ts) {
                if (t instanceof MapHandle) {
                    recordMap.put(((MapHandle) t).getPrimaryKey(), t);
                }
            }
        }
        return recordMap;
    }
}
