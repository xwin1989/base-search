package com.qeeka.domain.elastic.node;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kimi.lai on 12/22/2015.
 */
public class HighlightNode {
    @XmlElement(name = "pre_tags")
    private List<String> preTags;

    @XmlElement(name = "post_tags")
    private List<String> postTags;

    @XmlElement(name = "fields")
    private Map<String, Map<String, Object>> fields;

    public HighlightNode addHighlightFields(String... columnNames) {
        if (fields == null) {
            fields = new HashMap<>();
        }
        for (String columnName : columnNames) {
            fields.put(columnName, new HashMap<String, Object>());
        }
        return this;
    }

    public HighlightNode addHighlightPreTag(String... tagNames) {
        if (preTags == null) {
            preTags = new ArrayList<>();
        }
        for (String tag : tagNames) {
            preTags.add(tag);
        }
        return this;
    }

    public HighlightNode addHighlightPostTag(String... tagNames) {
        if (postTags == null) {
            postTags = new ArrayList<>();
        }
        for (String tag : tagNames) {
            postTags.add(tag);
        }
        return this;
    }
}
