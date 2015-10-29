package com.qeeka.domain.elastic.custom;

import com.qeeka.domain.elastic.ESTree;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by neal.xu on 2015/10/26
 */
public class ESRangeNode implements ESTree {
    @XmlElement(name = "range")
    private Map<String, Map<String, Object>> range;

    public ESRangeNode(String fieldName, Object gtValue, Boolean gtEquals, Object ltValue, Boolean ltEquals) {
        Map<String, Object> rangeMap = new HashMap<>();
        if (gtValue != null) {
            if (gtEquals) {
                rangeMap.put("gte", gtValue);
            } else {
                rangeMap.put("gt", gtValue);
            }
        }
        if (ltValue != null) {
            if (ltEquals) {
                rangeMap.put("lte", ltValue);
            } else {
                rangeMap.put("lt", ltValue);
            }
        }
        this.range = Collections.singletonMap(fieldName, rangeMap);
    }

    public Map<String, Map<String, Object>> getRange() {
        return range;
    }

    public void setRange(Map<String, Map<String, Object>> range) {
        this.range = range;
    }
}
