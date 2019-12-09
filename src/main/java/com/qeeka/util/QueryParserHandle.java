package com.qeeka.util;

import com.qeeka.domain.EntityInfo;
import com.qeeka.domain.QueryModel;
import com.qeeka.domain.Sort;
import com.qeeka.enums.Direction;
import com.qeeka.query.Join;
import com.qeeka.query.Query;
import org.springframework.util.CollectionUtils;

/**
 * Created by neal.xu on 2019/07/31.
 */
public class QueryParserHandle {
    private QueryParserHandle() {
    }

    public static QueryModel parse(Query query, Class clazz) {

        EntityInfo entityInfo = EntityHandle.getEntityInfo(clazz);
        QueryModel model = CriteriaParserHandle.parse(query.getCriteria());

        //append statement
        model.setTableStatement(parseTable(query, entityInfo));
        model.setSelectStatement(parseSelect(query, entityInfo));
        model.setCountStatement(parseCount(query));
        model.setOrderStatement(parseOrder(query));
        model.setGroupStatement(parseGroup(query));
        model.setPageableStatement(parsePageable(query));
        return model;
    }

    private static String parseSelect(Query query, EntityInfo entityInfo) {
        if (!query.isNeedRecord()) return null;

        StringBuilder sql = new StringBuilder(128);
        if (query.isNeedDistinct()) {
            sql.append("DISTINCT ");
        }
        String select = query.getSelects();
        if (select == null) {
            if (CollectionUtils.isEmpty(query.getJoinChain())) {
                select = entityInfo.getDefaultColumnStr();
            } else {
                select = EntityHandle.convertColumnMapping(entityInfo, true);
            }
        }
        sql.append(select);
        return sql.toString();
    }

    private static String parseCount(Query query) {
        if (!query.isNeedCount()) return null;
        StringBuilder sql = new StringBuilder(32);
        if (query.isNeedDistinct()) {
            sql.append("COUNT(DISTINCT ").append(query.getSelects()).append(')');
        } else {
            sql.append("COUNT(1)");
        }
        return sql.toString();
    }

    private static String parseTable(Query query, EntityInfo entityInfo) {

        StringBuilder sql = new StringBuilder(64);
        sql.append(" FROM ").append(entityInfo.getTableName());
        if (!CollectionUtils.isEmpty(query.getJoinChain())) {
            sql.append(" AS E");
            for (Join join : query.getJoinChain()) {
                sql.append(join.getLinkOperate().getValue()).append(join.getName()).append(" AS ").append(join.getAlias());
                QueryModel model = CriteriaParserHandle.parse(join.getCriteria());
                sql.append(" ON ").append(model.getConditionStatement());
            }
        }
        return sql.toString();
    }

    private static String parseOrder(Query query) {
        if (query.getSort() == null) return null;

        StringBuilder orderStatement = new StringBuilder(32);
        for (Sort.Order order : query.getSort()) {
            if (order.getDirection().equals(Direction.ASC_NULL) || order.getDirection().equals(Direction.DESC_NULL)) {
                orderStatement.append("ISNULL(").append(order.getProperty()).append(") ").append(order.getDirection().getValue()).append(',');
            } else if (order.getDirection().equals(Direction.ASC_FIELD)) {
                orderStatement.append("FIELD(").append(order.getProperty()).append(") ASC,");
            } else if (order.getDirection().equals(Direction.DESC_FIELD)) {
                orderStatement.append("FIELD(").append(order.getProperty()).append(") DESC,");
            } else {
                orderStatement.append(order.getProperty()).append(' ').append(order.getDirection().getValue()).append(',');
            }
        }
        if (orderStatement.length() > 0) {
            orderStatement.setLength(orderStatement.length() - 1);
        }
        return orderStatement.toString();
    }

    private static String parseGroup(Query query) {
        if (query.getGroupBy() == null) return null;

        StringBuilder sql = new StringBuilder(32);
        sql.append(" GROUP BY ");
        sql.append(EntityHandle.convertColumnMapping(query.getGroupBy().getSelects()));
        if (query.getGroupBy().getHavingCriteria() != null) {
            QueryModel queryModel = CriteriaParserHandle.parse(query.getGroupBy().getHavingCriteria());
            sql.append(" HAVING ").append(queryModel.getConditionStatement());
        }
        return sql.toString();
    }


    //only support limit
    private static String parsePageable(Query query) {
        if (query.getOffset() == null && query.getSize() == null) return null;

        StringBuilder sql = new StringBuilder(32);
        sql.append(" LIMIT ");
        if (query.getOffset() != null) {
            sql.append(query.getOffset()).append(',');
        }
        if (query.getSize() != null) {
            sql.append(query.getSize());
        }
        return sql.toString();
    }

}
