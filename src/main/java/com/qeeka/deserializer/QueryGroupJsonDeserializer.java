package com.qeeka.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.qeeka.domain.QueryGroup;
import com.qeeka.domain.QueryNode;
import com.qeeka.domain.QueryOperateNode;
import com.qeeka.operate.QueryLinkOperate;
import com.qeeka.operate.QueryOperate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by neal.xu on 8/1 0001.
 */
public class QueryGroupJsonDeserializer extends JsonDeserializer<QueryGroup> {
    @Override
    public QueryGroup deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
        Root root = jp.readValueAs(Root.class);

        QueryGroup group = new QueryGroup();
        if (root != null && root.queryHandleList != null) {
            List<QueryHandle> queryHandleList = root.queryHandleList;
            for (QueryHandle handle : queryHandleList) {
                if (handle.queryLinkOperate == null) {
                    group.getQueryHandleList().add(new QueryNode(handle.columnName, handle.value, handle.queryOperate));
                } else {
                    group.getQueryHandleList().add(new QueryOperateNode(handle.queryLinkOperate));
                }
            }
        }
        return group;
    }

    private static class Root {
        public List<QueryHandle> queryHandleList = new ArrayList<>();

        public void setQueryHandleList(List<QueryHandle> queryHandleList) {
            this.queryHandleList = queryHandleList;
        }
    }

    private static class QueryHandle {
        private String columnName;
        private Object value;
        private QueryOperate queryOperate;
        private QueryLinkOperate queryLinkOperate;

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public void setQueryOperate(QueryOperate queryOperate) {
            this.queryOperate = queryOperate;
        }

        public void setQueryLinkOperate(QueryLinkOperate queryLinkOperate) {
            this.queryLinkOperate = queryLinkOperate;
        }
    }
}
