package com.qeeka.repository;

import com.qeeka.domain.MappingNode;
import com.qeeka.domain.QueryGroup;
import com.qeeka.domain.QueryModel;
import com.qeeka.domain.QueryNode;
import com.qeeka.domain.QueryParser;
import com.qeeka.domain.UpdateGroup;
import com.qeeka.http.QueryRequest;
import com.qeeka.http.QueryResponse;
import com.qeeka.jdbc.BeanRowMapper;
import com.qeeka.operate.QueryResultType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.util.StringUtils;

import javax.persistence.Table;
import javax.sql.DataSource;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Neal on 16/4/7.
 */
public abstract class BaseJdbcRepository<T> extends BaseSearchRepository<T> {

    protected NamedParameterJdbcTemplate jdbcTemplate;
    protected String tableName;

    public BaseJdbcRepository() {
        super();
        String tName = entityClass.getSimpleName();
        Table annotation = entityClass.getAnnotation(Table.class);
        if (annotation != null && annotation.name() != null && !"".equals(annotation.name())) {
            tName = annotation.name();
        }
        this.tableName = tName;
    }

    /**
     * query all, return T
     *
     * @return
     */
    public QueryResponse<T> query() {
        return query(new QueryRequest(new QueryGroup()), entityClass);
    }

    /**
     * query all, return X
     *
     * @return
     */
    public <X> QueryResponse<X> query(Class<X> clazz) {
        return query(new QueryRequest(new QueryGroup()), clazz);
    }

    /**
     * query by query group, return X
     *
     * @param queryGroup
     * @param clazz
     * @param <X>
     * @return
     */
    public <X> QueryResponse<X> query(QueryGroup queryGroup, Class<X> clazz) {
        return query(new QueryRequest(queryGroup), clazz);
    }

    /**
     * query by group, return T
     *
     * @param queryGroup
     * @return
     */
    public QueryResponse<T> query(QueryGroup queryGroup) {
        return query(new QueryRequest(queryGroup), entityClass);
    }

    /**
     * query by query request, return T
     *
     * @param queryRequest
     * @return
     */
    public QueryResponse<T> query(QueryRequest queryRequest) {
        return query(queryRequest, entityClass);
    }

    /**
     * query by query request
     *
     * @param queryRequest
     * @param clazz
     * @param <X>
     * @return
     */
    public <X> QueryResponse<X> query(QueryRequest queryRequest, Class<X> clazz) {
        //parse query group to simple query domain
        QueryGroup queryGroup = queryRequest.getQueryGroup();
        QueryModel model = queryParser.parse(queryGroup);
        StringBuilder conditionSql = new StringBuilder();

        conditionSql.append(" FROM ").append(tableName).append(" AS E ");
        buildEntityMapping(queryGroup, conditionSql, model);

        if (StringUtils.hasText(model.getStatement())) {
            conditionSql.append(" WHERE ").append(model.getStatement());
        }
        if (StringUtils.hasText(model.getOrderStatement())) {
            conditionSql.append(" ORDER BY ").append(model.getOrderStatement());
        }

        if (QueryResultType.LIST.equals(queryRequest.getQueryResultType())) {
            return listQuery(queryRequest, model, conditionSql, clazz);
        } else {
            return singleQuery(queryRequest, model, conditionSql, queryRequest.getQueryResultType(), clazz);
        }
    }

    private <X> QueryResponse<X> listQuery(QueryRequest queryRequest, QueryModel model, StringBuilder conditionSql, Class<X> clazz) {
        QueryResponse<X> queryResponse = new QueryResponse<>();
        if (queryRequest.isNeedRecord()) {
            StringBuilder sql = new StringBuilder("SELECT ");
            if (queryRequest.isNeedDistinct()) {
                sql.append("DISTINCT ").append(queryRequest.getSelects());
            } else {
                sql.append(queryRequest.getSelects());
            }
            sql.append(conditionSql);
            //Page search , need page index and size
            if (queryRequest.getPageIndex() != 0 || queryRequest.getPageSize() != 0) {
                sql.append(" limit ");
                if (queryRequest.getPageIndex() != 0) {
                    sql.append(queryRequest.getPageIndex() * queryRequest.getPageSize()).append(',');
                }
                sql.append(queryRequest.getPageSize());
            }
            List<X> list = query(sql, model.getParameters(), clazz);
            //Set query record
            queryResponse.setRecords(list);
            queryResponse.setPageIndex(queryRequest.getPageIndex());
            queryResponse.setPageSize(queryRequest.getPageSize());
        }
        //Query count
        if (queryRequest.isNeedCount()) {
            StringBuilder countSql = new StringBuilder("SELECT ");
            if (queryRequest.isNeedDistinct()) {
                countSql.append("COUNT(DISTINCT ").append(queryRequest.getSelects()).append(')');
            } else {
                countSql.append("COUNT(1)");
            }
            countSql.append(conditionSql);
            BigInteger total = queryForObject(countSql, model.getParameters(), BigInteger.class);
            queryResponse.setTotalRecords(total.longValue());
        }
        return queryResponse;
    }


    /**
     * build jdbc entity join logic
     *
     * @param queryGroup
     * @param sql
     * @param sql
     */
    private void buildEntityMapping(QueryGroup queryGroup, StringBuilder sql, QueryModel model) {
        if (queryGroup == null || queryGroup.getEntityMapping() == null) {
            return;
        }
        for (MappingNode node : queryGroup.getEntityMapping()) {
            sql.append(node.getLinkOperate().getValue()).
                    append(node.getEntityName()).append(" AS ").append(node.getEntityAlias());

            Iterator<QueryNode> iterator = node.getLinkMapping().iterator();
            if (iterator.hasNext()) {
                sql.append(" ON ");
                QueryNode next = iterator.next();
                CharSequence queryPart = QueryParser.generateParameterHql(next, model.getParameters());
                sql.append(queryPart);
            }
            while (iterator.hasNext()) {
                QueryNode next = iterator.next();
                CharSequence queryPart = QueryParser.generateParameterHql(next, model.getParameters());
                sql.append(" AND ").append(queryPart);
            }
        }
    }

    private <X> QueryResponse<X> singleQuery(QueryRequest queryRequest, QueryModel model, StringBuilder conditionSql, QueryResultType queryResultType, Class<X> clazz) {
        QueryResponse<X> queryResponse = new QueryResponse<>();
        StringBuilder sql = new StringBuilder("SELECT ");
        if (queryRequest.isNeedDistinct()) {
            sql.append("DISTINCT ").append(queryRequest.getSelects());
        } else {
            sql.append(queryRequest.getSelects());
        }
        sql.append(conditionSql);
        if (QueryResultType.SINGLE.equals(queryResultType))
            queryResponse.setEntity(querySingle(sql, model.getParameters(), clazz));
        else if (QueryResultType.UNIQUE.equals(queryResultType))
            queryResponse.setEntity(queryUnique(sql, model.getParameters(), clazz));
        return queryResponse;
    }

    /**
     * find list by native query with sql
     *
     * @param sql
     * @return
     */
    public List<T> query(CharSequence sql) {
        return query(sql, null, entityClass);
    }

    /**
     * find list by native query with sql & entity class
     *
     * @param sql
     * @param clazz
     * @param <X>
     * @return
     */
    public <X> List<X> query(CharSequence sql, Class<X> clazz) {
        return query(sql, null, clazz);
    }


    public List<T> query(CharSequence sql, Map<String, ?> params) {
        return query(sql, params, entityClass);
    }


    /**
     * query list by native query with sql & params & offset & size & entity class
     *
     * @param sql
     * @param params
     * @return list
     */
    public <X> List<X> query(CharSequence sql, Map<String, ?> params, Class<X> clazz) {
        StopWatch watch = new StopWatch();
        int returnSize = 0;
        try {
            List<X> resultList = jdbcTemplate.query(sql.toString(), params, BeanRowMapper.forClass(clazz));
            returnSize = resultList.size();
            return resultList;
        } finally {
            logger.debug("native query, query={}, params={}, resultSize={}, elapsedTime={}", sql, params, returnSize, watch.elapsedTime());
        }
    }

    /**
     * query list mapping by rowMapper
     *
     * @param sql
     * @param rowMapper
     * @param <X>
     * @return
     */
    public <X> List<X> query(CharSequence sql, RowMapper<X> rowMapper) {
        return query(sql, null, rowMapper);
    }

    /**
     * query list with params & rowMapper
     *
     * @param sql
     * @param params
     * @param rowMapper
     * @param <X>
     * @return
     */
    public <X> List<X> query(CharSequence sql, Map<String, ?> params, RowMapper<X> rowMapper) {
        StopWatch watch = new StopWatch();
        int returnSize = 0;
        try {
            List<X> resultList = jdbcTemplate.query(sql.toString(), params, rowMapper);
            returnSize = resultList.size();
            return resultList;
        } finally {
            logger.debug("native query, query={}, params={}, resultSize={}, elapsedTime={}", sql, params, returnSize, watch.elapsedTime());
        }
    }


    /**
     * query unique with group, return X
     *
     * @param queryGroup
     * @param clazz
     * @param <X>
     * @return
     */
    public <X> X queryUnique(QueryGroup queryGroup, Class<X> clazz) {
        return query(new QueryRequest(queryGroup).uniqueResult(), clazz).getEntity();
    }

    /**
     * query unique with group, return T
     *
     * @param queryGroup
     * @return
     */

    public T queryUnique(QueryGroup queryGroup) {
        return query(new QueryRequest(queryGroup).uniqueResult(), entityClass).getEntity();
    }

    /**
     * query unique with query request
     *
     * @param queryRequest
     * @return
     */
    public T queryUnique(QueryRequest queryRequest) {
        return query(queryRequest.uniqueResult(), entityClass).getEntity();
    }

    /**
     * query unique with request & clazz
     *
     * @param queryRequest
     * @param clazz
     * @param <X>
     * @return
     */
    public <X> X queryUnique(QueryRequest queryRequest, Class<X> clazz) {
        return query(queryRequest.uniqueResult(), clazz).getEntity();
    }

    /**
     * find unique with sql
     *
     * @param sql
     * @return entity class
     */
    public T queryUnique(CharSequence sql) {
        return queryUnique(sql, null, entityClass);
    }

    /**
     * find unique with sql & class
     *
     * @param sql
     * @param clazz
     * @param <X>
     * @return object
     */
    public <X> X queryUnique(CharSequence sql, Class<X> clazz) {
        return queryUnique(sql, null, clazz);
    }

    /**
     * find unique with sql & params
     *
     * @param sql
     * @param params
     * @return entity class
     */
    public T queryUnique(CharSequence sql, Map<String, ?> params) {
        return queryUnique(sql, params, entityClass);
    }

    /**
     * find unique with sql & params & class
     *
     * @param sql
     * @param params
     * @param resultClass
     * @param <X>
     * @return object
     */
    public <X> X queryUnique(CharSequence sql, Map<String, ?> params, Class<X> resultClass) {
        StopWatch watch = new StopWatch();
        try {
            List<X> resultList = jdbcTemplate.query(sql.toString(), params, BeanRowMapper.forClass(resultClass));
            return getUniqueResult(resultList);
        } finally {
            logger.debug("native unique query, query={}, params={}, elapsedTime={}", sql, params, watch.elapsedTime());
        }
    }

    /**
     * find single with sql
     *
     * @param sql
     * @return entity class
     */
    public T querySingle(CharSequence sql) {
        return querySingle(sql, null, entityClass);
    }

    /**
     * find single with sql & class
     *
     * @param sql
     * @param clazz
     * @param <X>
     * @return object
     */
    public <X> X querySingle(CharSequence sql, Class<X> clazz) {
        return querySingle(sql, null, clazz);
    }

    /**
     * find single with sql & params
     *
     * @param sql
     * @param params
     * @return entity class
     */
    public T querySingle(CharSequence sql, Map<String, ?> params) {
        return querySingle(sql, params, entityClass);
    }

    /**
     * find single with sql & params & class
     *
     * @param sql
     * @param params
     * @param resultClass
     * @param <X>
     * @return
     */
    public <X> X querySingle(CharSequence sql, Map<String, ?> params, Class<X> resultClass) {
        StopWatch watch = new StopWatch();
        try {
            List<X> resultList = jdbcTemplate.query(sql.toString(), params, BeanRowMapper.forClass(resultClass));
            return getSingleResult(resultList);
        } finally {
            logger.debug("native unique query, query={}, params={}, elapsedTime={}", sql, params, watch.elapsedTime());
        }
    }

    /**
     * find single with request
     *
     * @param queryRequest
     * @return
     */
    public T querySingle(QueryRequest queryRequest) {
        return query(queryRequest.singleResult(), entityClass).getEntity();
    }

    /**
     * find single with request & clazz
     *
     * @param queryRequest
     * @param clazz
     * @param <X>
     * @return
     */
    public <X> X querySingle(QueryRequest queryRequest, Class<X> clazz) {
        return query(queryRequest.singleResult(), clazz).getEntity();
    }

    /**
     * find single with group
     *
     * @param queryGroup
     * @return
     */
    public T querySingle(QueryGroup queryGroup) {
        return query(new QueryRequest(queryGroup).singleResult(), entityClass).getEntity();
    }

    /**
     * find single with group & clazz
     *
     * @param queryGroup
     * @param clazz
     * @param <X>
     * @return
     */
    public <X> X querySingle(QueryGroup queryGroup, Class<X> clazz) {
        return query(new QueryRequest(queryGroup).singleResult(), clazz).getEntity();
    }

    /**
     * find list by JdbcTemplate with sql
     *
     * @param sql
     * @return Object list
     */
    public List<Object> queryForList(final CharSequence sql) {
        return queryForList(sql, null, Object.class);
    }

    /**
     * * find list by JdbcTemplate with sql & params
     *
     * @param sql
     * @return
     */
    public List<Object> queryForList(final CharSequence sql, Map<String, ?> params) {
        return queryForList(sql, params, Object.class);
    }

    /**
     * find list by JdbcTemplate with sql & class
     * The results will be mapped to a List (one entry for each row) of result objects
     *
     * @param sql
     * @param clazz
     * @param clazz the required type of element in the result list (for example, Integer.class) 
     * @return list
     */
    public <X> List<X> queryForList(final CharSequence sql, Class<X> clazz) {
        return queryForList(sql, null, clazz);
    }

    /**
     * find list by JdbcTemplate with sql & params & class
     * The results will be mapped to a List (one entry for each row) of result objects
     *
     * @param sql
     * @param params
     * @param clazz  the required type of element in the result list (for example, Integer.class) 
     * @param <X>
     * @return list
     */
    public <X> List<X> queryForList(final CharSequence sql, Map<String, ?> params, Class<X> clazz) {
        StopWatch watch = new StopWatch();
        try {
            return jdbcTemplate.queryForList(sql.toString(), params, clazz);
        } finally {
            logger.debug("queryForList, query={}, params={}, elapsedTime={}", sql, params, watch.elapsedTime());
        }
    }

    /**
     * find object by JdbcTemplate with sql & class
     * Execute a query for a result object
     *
     * @param sql
     * @param clazz
     * @param <X>
     * @return object
     */
    public <X> X queryForObject(final CharSequence sql, Class<X> clazz) {
        return queryForObject(sql, null, clazz);
    }

    /**
     * find object by JdbcTemplate by sql & params & class
     * Execute a query for a result object
     *
     * @param sql
     * @param params
     * @param clazz
     * @param <X>
     * @return object
     */
    public <X> X queryForObject(final CharSequence sql, Map<String, ?> params, Class<X> clazz) {
        StopWatch watch = new StopWatch();
        try {
            return jdbcTemplate.queryForObject(sql.toString(), params, clazz);
        } finally {
            logger.debug("queryForObject, query={}, params={}, elapsedTime={}", sql, params, watch.elapsedTime());
        }
    }

    /**
     * find object by JdbcTemplate with sql
     *
     * @param sql
     * @return map
     */
    public Map<String, Object> queryForMap(final CharSequence sql) {
        return queryForMap(sql, null);
    }

    /**
     * find object by JdbcTemplate with sql & params
     *
     * @param sql
     * @param params
     * @return map
     */
    public Map<String, Object> queryForMap(final CharSequence sql, Map<String, ?> params) {
        StopWatch watch = new StopWatch();
        int returnSize = 0;
        try {
            Map<String, Object> queryForMap = jdbcTemplate.queryForMap(sql.toString(), params);
            returnSize = queryForMap.size();
            return queryForMap;
        } finally {
            logger.debug("queryForMap, query={}, params={}, resultSize={}, elapsedTime={}", sql, params, returnSize, watch.elapsedTime());
        }
    }

    /**
     * update by sql
     *
     * @param sql
     * @return
     */
    public int updateNative(CharSequence sql) {
        return updateNative(sql, null);
    }

    /**
     * update by sql with params
     *
     * @param sql
     * @param params
     * @return
     */
    public int updateNative(CharSequence sql, Map<String, ?> params) {
        StopWatch watch = new StopWatch();
        int size = 0;
        try {
            size = jdbcTemplate.update(sql.toString(), params);
            return size;
        } finally {
            logger.debug("native update, query={}, updateSize={}, elapsedTime={}", sql, size, watch.elapsedTime());
        }
    }

    public int updateNative(UpdateGroup group) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = convertUpdateGroup(group, params);
        if (group.getQueryGroup() != null) {
            QueryModel queryModel = queryParser.parse(group.getQueryGroup());
            sql.append(" WHERE ").append(queryModel.getStatement());
            params.putAll(queryModel.getParameters());
        }
        return updateNative(sql, params);
    }

    public int[] batchUpdateNative(CharSequence sql, List<?> objects) {
        StopWatch watch = new StopWatch();
        SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(objects.toArray());
        try {
            return jdbcTemplate.batchUpdate(sql.toString(), params);
        } finally {
            logger.debug("native batch update, query={}, updateSize={}, elapsedTime={}", sql, objects.size(), watch.elapsedTime());
        }
    }

    public int[] batchUpdateNative(CharSequence sql, Map<String, ?>[] batchValues) {
        StopWatch watch = new StopWatch();
        try {
            return jdbcTemplate.batchUpdate(sql.toString(), batchValues);
        } finally {
            logger.debug("native batch update, query={}, updateSize={}, elapsedTime={}", sql, batchValues.length, watch.elapsedTime());
        }
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
}
