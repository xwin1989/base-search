package com.qeeka.domain.elastic;

import com.qeeka.domain.elastic.custom.ESGroupByNode;
import com.qeeka.domain.elastic.group.ESAggsGroup;
import com.qeeka.domain.elastic.group.ESFilteredGroup;
import com.qeeka.domain.elastic.group.ESQueryGroup;
import com.qeeka.domain.elastic.node.ESFilterNode;
import com.qeeka.domain.elastic.node.ESQueryNode;
import com.qeeka.operate.Direction;
import com.qeeka.util.QueryJSONBinder;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by neal.xu on 2015/10/20
 */
public class ESSearchGroup {
    @XmlElement(name = "from")
    private Integer from;

    @XmlElement(name = "size")
    private Integer size;

    @XmlElement(name = "query")
    private ESQueryGroup query;

    @XmlElement(name = "sort")
    private List<Map<String, Map<String, String>>> sort;

    @XmlElement(name = "aggs")
    private ESAggsGroup aggs;

    private void checkQueryNodeStatus() {
        if (query == null) {
            query = new ESQueryGroup();
        }
        if (query.getFiltered() == null) {
            query.setFiltered(new ESFilteredGroup());
        }
    }

    public ESSearchGroup addSort(String columnName, Direction direction) {
        if (sort == null) {
            sort = new ArrayList<>();
        }
        sort.add(Collections.singletonMap(columnName, Collections.singletonMap("order", direction.getValue().toLowerCase())));
        return this;
    }

    public Integer getFrom() {
        return from;
    }

    public ESSearchGroup setFrom(Integer from) {
        this.from = from;
        return this;
    }

    public Integer getSize() {
        return size;
    }

    public ESSearchGroup setSize(Integer size) {
        this.size = size;
        return this;
    }

    public ESQueryNode generateQueryNode() {
        checkQueryNodeStatus();
        if (this.query.getFiltered().getQuery() != null) {
            return this.query.getFiltered().getQuery();
        }
        ESQueryNode queryNode = new ESQueryNode();
        this.query.getFiltered().setQuery(queryNode);
        return queryNode;
    }

    public ESFilterNode generateFilterNode() {
        checkQueryNodeStatus();
        if (this.query.getFiltered().getFilter() != null) {
            return this.query.getFiltered().getFilter();
        }
        ESFilterNode filterNode = new ESFilterNode();
        this.query.getFiltered().setFilter(filterNode);
        return filterNode;
    }

    public ESGroupByNode generateGroupByNode() {
        if (this.aggs == null) {
            this.aggs = new ESAggsGroup();
        }
        if (this.aggs.getGroupByNode() != null) {
            return this.aggs.getGroupByNode();
        }
        ESGroupByNode groupByNode = new ESGroupByNode();
        this.aggs.setGroupByNode(groupByNode);
        return groupByNode;
    }

    public String generateScript() {
        checkQueryNodeStatus();
        ESFilteredGroup filtered = this.query.getFiltered();
        if (filtered.getQuery() != null && filtered.getQuery().getBoolNode() == null) {
            filtered.setQuery(null);
        }
        if (filtered.getFilter() != null && filtered.getFilter().getBoolNode() == null) {
            filtered.setFilter(null);
        }
        return QueryJSONBinder.binder(ESSearchGroup.class).toJSON(this);
    }
}
