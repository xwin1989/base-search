package com.qeeka.query;

import com.qeeka.SFunction;

/**
 * Created by neal.xu on 2019/11/21.
 */
public class CriteriaWrapper<T> {
    public CriteriaWrapper<T> key(SFunction<T, ?> column) {
        return this;
    }
}
