package com.qeeka.repository;

import com.qeeka.annotation.Entity;
import com.qeeka.annotation.Id;
import com.qeeka.domain.EntityInfo;
import com.qeeka.domain.QueryGroup;
import com.qeeka.domain.QueryModel;
import com.qeeka.domain.QueryResponse;
import com.qeeka.domain.StopWatch;
import com.qeeka.domain.UpdateGroup;
import com.qeeka.domain.UpdateNode;
import com.qeeka.enums.GenerationType;
import com.qeeka.query.Criteria;
import com.qeeka.query.Join;
import com.qeeka.query.Query;
import com.qeeka.util.CriteriaParserHandle;
import com.qeeka.util.EntityHandle;
import com.qeeka.util.QueryParserHandle;
import com.qeeka.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Neal on 16/4/7.
 */
public abstract class BaseJdbcRepository<T> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private NamedParameterJdbcTemplate jdbcTemplate;
    private final Class<T> entityClass;

    //get default class
    public BaseJdbcRepository() {
        Class<?>[] arguments = GenericTypeResolver.resolveTypeArguments(getClass(), BaseJdbcRepository.class);
        if (arguments == null || arguments.length != 1) {
            throw new IllegalArgumentException(MessageFormatter.format("repository must extend with generic type like BaseSearchRepository<T>, class={}", getClass()).getMessage());
        }
        entityClass = (Class<T>) arguments[0];
        if (entityClass == null || !entityClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("repository must extend with generic type like repository<T extends Entity>");
        }
    }

//--------------------------- get operator -------------------------

    public T get(Object id) {
        EntityInfo entityInfo = EntityHandle.getEntityInfo(entityClass);
        return queryUnique(Query.query(Criteria.where(entityInfo.getIdColumn()).eq(id)));
    }

    public <X> X get(Object id, Class<X> clazz) {
        EntityInfo entityInfo = EntityHandle.getEntityInfo(clazz);
        return queryUnique(Query.query(Criteria.where(entityInfo.getIdColumn()).eq(id)), clazz);
    }

    public T get(String columnName, Object value) {
        return queryUnique(Query.query(Criteria.where(columnName).eq(value)));
    }

//--------------------------- delete operator -------------------------


    public int deleteById(Object id) {
        return deleteById(id, entityClass);
    }

    public <X> int deleteById(Object id, Class<X> clazz) {
        EntityInfo entityInfo = EntityHandle.getEntityInfo(clazz);
        StringBuilder sql = new StringBuilder(64);
        sql.append("DELETE FROM ").append(entityInfo.getTableName()).append(" WHERE ")
                .append(entityInfo.getIdColumn()).append(" = :ENTITY_ID");
        return update(sql, Collections.singletonMap("ENTITY_ID", id));
    }

    public int delete(Object entity) {
        if (entity == null) return 0;
        Class<?> clazz = entity.getClass();
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("entity miss Entity");
        }
        EntityInfo entityInfo = EntityHandle.getEntityInfo(clazz);
        try {
            Field idField = ReflectionUtil.findUniqueFieldWithAnnotation(clazz, Id.class);
            StringBuilder sql = new StringBuilder(64);
            sql.append("DELETE FROM ").append(entityInfo.getTableName()).append(" WHERE ")
                    .append(entityInfo.getIdColumn()).append(" = :ENTITY_ID");
            return update(sql, Collections.singletonMap("ENTITY_ID", idField.get(entity)));
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }
        return 0;
    }

    public int delete(Criteria criteria) {
        EntityInfo entityInfo = EntityHandle.getEntityInfo(entityClass);
        StringBuilder sql = new StringBuilder(128);

        sql.append("DELETE FROM ").append(entityInfo.getTableName());
        QueryModel queryModel = CriteriaParserHandle.parse(criteria);
        sql.append(" WHERE ").append(queryModel.getConditionStatement());
        return update(sql, queryModel.getParameters());
    }

//--------------------------- save operator -------------------------

    public <X> X save(X entity) {
        //reflection
        EntityInfo entityInfo = EntityHandle.getEntityInfo(entity.getClass());
        Map<String, String> columnMap = entityInfo.getColumnMap();
        Map<String, Field> allDeclareFields = ReflectionUtil.getAllDeclareFields(entity.getClass());

        //begin build sql
        StringBuilder sql = new StringBuilder(128);
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        sql.append("INSERT INTO ").append(entityInfo.getTableName()).append('(');

        boolean needHolder = false;
        for (Map.Entry<String, Field> entry : allDeclareFields.entrySet()) {
            String columnName = columnMap.get(entry.getKey());
            Field field = entry.getValue();
            if (entityInfo.getIdColumn().equals(columnName)) {
                if (GenerationType.IDENTITY.equals(entityInfo.getStrategy())) {
                    needHolder = true;
                    continue;
                }
                try {
                    if (field.get(entity) == null) {
                        throw new IllegalArgumentException("primary id can't null");
                    }
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (columnName == null) continue;
            try {
                params.put(columnName, field.get(entity));
                sql.append(columnName).append(',');
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            }
        }
        sql.setLength(sql.length() - 1);
        sql.append(") VALUES (");
        for (String key : params.keySet()) {
            sql.append(':').append(key).append(',');
        }
        sql.setLength(sql.length() - 1);
        sql.append(')');

        if (needHolder) {
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            update(sql.toString(), params, holder);
            Number key = holder.getKey();
            if (key == null) return entity;
            Field idField = ReflectionUtil.findUniqueFieldWithAnnotation(entity.getClass(), Id.class);
            try {
                Class type = idField.getType();
                idField.set(entity, NumberUtils.convertNumberToTargetClass(key, type));
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            update(sql, params);
        }
        return entity;
    }

    public <X> List<X> batchSave(List<X> entityList) {
        for (X x : entityList) {
            save(x);
        }
        return entityList;
    }

//--------------------------- count operator -------------------------

    public Long count() {
        return count(new QueryGroup(), entityClass);
    }

    public <X> Long count(Class<X> clazz) {
        return count(new QueryGroup(), clazz);
    }

    public Long count(QueryGroup group) {
        return count(group, entityClass);
    }

    public <X> Long count(QueryGroup group, Class<X> clazz) {
//        return query(group.onlyCount(), clazz).getTotal();
        return 0L;
    }

//--------------------------- QueryResponse operator -------------------------

    /**
     * query all, return T
     */
    public QueryResponse<T> query() {
        return query(Query.query(), entityClass);
    }

    /**
     * query all, return X
     */
    public <X> QueryResponse<X> query(Class<X> clazz) {
        return query(Query.query(), clazz);
    }

    /**
     * query by group, return T class
     */
    public QueryResponse<T> query(Query query) {
        return query(query, entityClass);
    }

    /**
     * query by query request, Specifies return class
     */
    public <X> QueryResponse<X> query(Query query, Class<X> clazz) {
        QueryResponse<X> queryResponse = new QueryResponse<>();
        QueryModel queryModel = QueryParserHandle.parse(query, clazz);
        if (queryModel.getSelectStatement() != null) {
            StringBuilder sql = new StringBuilder(128);
            sql.append("SELECT ").append(queryModel.getSelectStatement());
            sql.append(" FROM ").append(queryModel.getTableStatement());
            if (queryModel.getConditionStatement() != null)
                sql.append(" WHERE ").append(queryModel.getConditionStatement());
            if (queryModel.getOrderStatement() != null)
                sql.append(" ORDER BY ").append(queryModel.getOrderStatement());
            if (queryModel.getGroupStatement() != null)
                sql.append(" GROUP BY ").append(queryModel.getGroupStatement());
            if (queryModel.getPageableStatement() != null)
                sql.append(queryModel.getPageableStatement());

            queryResponse.setRecords(query(sql, queryModel.getParameters(), clazz));
        }
        if (queryModel.getCountStatement() != null) {
            StringBuilder sql = new StringBuilder(128);
            sql.append("SELECT ").append(queryModel.getCountStatement());
            sql.append(" FROM ").append(queryModel.getTableStatement());

            queryResponse.setTotal(queryForObject(sql, queryModel.getParameters(), BigInteger.class).longValue());
        }
        return queryResponse;
    }


    //--------------------------- query sql operator -------------------------
    public List<T> query(CharSequence sql) {
        return query(sql, null, entityClass);
    }

    public <X> List<X> query(CharSequence sql, Class<X> clazz) {
        return query(sql, null, clazz);
    }

    public List<T> query(CharSequence sql, Map<String, ?> params) {
        return query(sql, params, entityClass);
    }

    public <X> List<X> query(CharSequence sql, RowMapper<X> rowMapper) {
        return query(sql, null, rowMapper);
    }

    /**
     * query list , Specifies params & class
     */
    public <X> List<X> query(CharSequence sql, Map<String, ?> params, Class<X> clazz) {
        return query(sql, params, BeanPropertyRowMapper.newInstance(clazz));
    }

    /**
     * query list , Specifies params & rowMapper
     */
    public <X> List<X> query(CharSequence sql, Map<String, ?> params, RowMapper<X> rowMapper) {
        StopWatch watch = new StopWatch();
        int returnSize = 0;
        try {
            List<X> resultList = jdbcTemplate.query(sql.toString(), params, rowMapper);
            returnSize = resultList.size();
            return resultList;
        } finally {
            logger.debug("query, sql={}, params={}, resultSize={}, elapsedTime={}", sql, params, returnSize, watch.elapsedTime());
        }
    }

//--------------------------- query unique operator -------------------------

    /**
     * query unique with group, return T
     */
    public T queryUnique(Query query) {
        return queryUnique(query, entityClass);
    }

    /**
     * query unique with group, return X
     */
    public <X> X queryUnique(Query query, Class<X> clazz) {
        query.size(1);
        return getUniqueResult(query(query, clazz).getRecords());
    }

    /**
     * find unique with sql
     */
    public T queryUnique(CharSequence sql) {
        return queryUnique(sql, null, entityClass);
    }

    /**
     * find unique with sql & class
     */
    public <X> X queryUnique(CharSequence sql, Class<X> clazz) {
        return queryUnique(sql, null, clazz);
    }

    /**
     * find unique with sql & params
     */
    public T queryUnique(CharSequence sql, Map<String, ?> params) {
        return queryUnique(sql, params, entityClass);
    }

    /**
     * find unique with sql & params & class
     */
    public <X> X queryUnique(CharSequence sql, Map<String, ?> params, Class<X> clazz) {
        return queryUnique(sql, params, BeanPropertyRowMapper.newInstance(clazz));
    }

    public <X> X queryUnique(CharSequence sql, RowMapper<X> rowMapper) {
        return queryUnique(sql, null, rowMapper);
    }

    /**
     * find unique with sql & params & class
     */
    public <X> X queryUnique(CharSequence sql, Map<String, ?> params, RowMapper<X> rowMapper) {
        return getUniqueResult(query(sql, params, rowMapper));
    }

//--------------------------- query single operator -------------------------

    /**
     * query unique with group, return T
     */
    public T querySingle(Query query) {
        return querySingle(query, entityClass);
    }

    /**
     * query unique with group, return X
     */
    public <X> X querySingle(Query query, Class<X> clazz) {
        query.size(1);
        return getSingleResult(query(query, clazz).getRecords());
    }

    public T querySingle(CharSequence sql) {
        return querySingle(sql, null, entityClass);
    }

    public <X> X querySingle(CharSequence sql, Class<X> clazz) {
        return querySingle(sql, null, clazz);
    }

    public T querySingle(CharSequence sql, Map<String, ?> params) {
        return querySingle(sql, params, entityClass);
    }


    public <X> X querySingle(CharSequence sql, RowMapper<X> rowMapper) {
        return querySingle(sql, null, rowMapper);
    }

    public <X> X querySingle(CharSequence sql, Map<String, ?> params, Class<X> clazz) {
        return querySingle(sql, params, BeanPropertyRowMapper.newInstance(clazz));
    }

    /**
     * find single with sql & params & class
     */
    public <X> X querySingle(CharSequence sql, Map<String, ?> params, RowMapper<X> rowMapper) {
        return getSingleResult(query(sql, params, rowMapper));
    }

//--------------------------- List&Map operator -------------------------

    /**
     * find list by JdbcTemplate with sql & class
     * The results will be mapped to a List (one entry for each row) of result objects
     */
    public <X> List<X> queryForList(final CharSequence sql, Class<X> clazz) {
        return queryForList(sql, null, clazz);
    }

    /**
     * find list by JdbcTemplate with sql & params & class
     * The results will be mapped to a List (one entry for each row) of result objects
     */
    public <X> List<X> queryForList(final CharSequence sql, Map<String, ?> params, Class<X> clazz) {
        StopWatch watch = new StopWatch();
        try {
            return jdbcTemplate.queryForList(sql.toString(), params, clazz);
        } finally {
            logger.debug("queryForList, sql={}, params={}, elapsedTime={}", sql, params, watch.elapsedTime());
        }
    }

    /**
     * find object by JdbcTemplate with sql & class
     * Execute a query for a result object
     */
    public <X> X queryForObject(final CharSequence sql, Class<X> clazz) {
        return queryForObject(sql, null, clazz);
    }

    /**
     * find object by JdbcTemplate by sql & params & class
     * Execute a query for a result object
     */
    public <X> X queryForObject(final CharSequence sql, Map<String, ?> params, Class<X> clazz) {
        StopWatch watch = new StopWatch();
        try {
            return jdbcTemplate.queryForObject(sql.toString(), params, clazz);
        } finally {
            logger.debug("queryForObject, sql={}, params={}, elapsedTime={}", sql, params, watch.elapsedTime());
        }
    }

    /**
     * find object by JdbcTemplate with sql
     */
    public Map<String, Object> queryForMap(final CharSequence sql) {
        return queryForMap(sql, null);
    }

    /**
     * find object by JdbcTemplate with sql & params
     */
    public Map<String, Object> queryForMap(final CharSequence sql, Map<String, ?> params) {
        StopWatch watch = new StopWatch();
        int returnSize = 0;
        try {
            Map<String, Object> queryForMap = jdbcTemplate.queryForMap(sql.toString(), params);
            returnSize = queryForMap.size();
            return queryForMap;
        } finally {
            logger.debug("queryForMap, sql={}, params={}, resultSize={}, elapsedTime={}", sql, params, returnSize, watch.elapsedTime());
        }
    }

//--------------------------- update group operator -------------------------

    public int update(UpdateGroup group) {
        return update(group, entityClass);
    }

    public <X> int update(UpdateGroup group, Class<X> clazz) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = convertUpdateGroup(group, params, clazz);
        if (group.getQueryGroup() != null) {
            QueryModel queryModel = null;//CriteriaParserHandle.parse(group.getQueryGroup());
            sql.append(" WHERE ").append(queryModel.getConditionStatement());
            params.putAll(queryModel.getParameters());
        }
        return update(sql, params);
    }

    /**
     * update by sql
     */
    public int update(CharSequence sql) {
        return update(sql, null);
    }

    /**
     * update by sql with params
     */
    public int update(CharSequence sql, Map<String, ?> params) {
        StopWatch watch = new StopWatch();
        int size = 0;
        try {
            size = jdbcTemplate.update(sql.toString(), params);
            return size;
        } finally {
            logger.debug("update, sql={}, params={}, updateSize={}, elapsedTime={}", sql, params, size, watch.elapsedTime());
        }
    }

    private int update(CharSequence sql, Map<String, ?> params, GeneratedKeyHolder holder) {
        StopWatch watch = new StopWatch();
        int size = 0;
        try {
            size = jdbcTemplate.update(sql.toString(), new MapSqlParameterSource(params), holder);
            return size;
        } finally {
            logger.debug("update, sql={}, params={}, updateSize={}, elapsedTime={}", sql, params, size, watch.elapsedTime());
        }
    }

    public int[] batchUpdate(CharSequence sql, List<?> objects) {
        StopWatch watch = new StopWatch();
        SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(objects.toArray());
        try {
            return jdbcTemplate.batchUpdate(sql.toString(), params);
        } finally {
            logger.debug("batch update, sql={}, updateSize={}, elapsedTime={}", sql, objects.size(), watch.elapsedTime());
        }
    }

    public int[] batchUpdate(CharSequence sql, Map<String, ?>[] batchValues) {
        StopWatch watch = new StopWatch();
        try {
            return jdbcTemplate.batchUpdate(sql.toString(), batchValues);
        } finally {
            logger.debug("batch update, sql={}, params={}, updateSize={}, elapsedTime={}", sql, batchValues, batchValues.length, watch.elapsedTime());
        }
    }

    public <X> int update(X entity) {
        //reflection
        EntityInfo entityInfo = EntityHandle.getEntityInfo(entity.getClass());
        Map<String, Field> allDeclareFields = ReflectionUtil.getAllDeclareFields(entity.getClass());
        Map<String, String> columnMap = entityInfo.getColumnMap();

        //begin build sql
        StringBuilder sql = new StringBuilder(128);
        Map<String, Object> params = new HashMap<>();
        sql.append("UPDATE ").append(entityInfo.getTableName()).append(" SET ");

        for (Map.Entry<String, Field> entry : allDeclareFields.entrySet()) {
            String columnName = columnMap.get(entry.getKey());
            if (columnName == null) continue;
            Field field = entry.getValue();
            try {
                params.put(columnName, field.get(entity));
                //skip id column
                if (!columnName.equals(entityInfo.getIdColumn()))
                    sql.append(columnName).append(" = :").append(columnName).append(',');
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage(), e);
            }
        }
        sql.setLength(sql.length() - 1);
        sql.append(" WHERE ").append(entityInfo.getIdColumn()).append(" = :").append(entityInfo.getIdColumn());
        return update(sql.toString(), params);
    }

    public <X> int[] batchUpdate(List<X> entityList) {
        if (entityList.isEmpty()) return null;

        Class<?> clazz = entityList.get(0).getClass();
        EntityInfo entityInfo = EntityHandle.getEntityInfo(clazz);
        Map<String, Field> allDeclareFields = ReflectionUtil.getAllDeclareFields(clazz);
        Map<String, String> columnMap = entityInfo.getColumnMap();
        //begin build sql
        StringBuilder sql = new StringBuilder(64);
        sql.append("UPDATE ").append(entityInfo.getTableName()).append(" SET ");
        for (Map.Entry<String, Field> entry : allDeclareFields.entrySet()) {
            String columnName = columnMap.get(entry.getKey());
            if (columnName == null) continue;
            //skip id column
            if (entityInfo.getIdColumn().equals(columnName)) continue;
            sql.append(columnName).append(" = :").append(columnName).append(',');
        }
        sql.setLength(sql.length() - 1);
        sql.append(" WHERE ").append(entityInfo.getIdColumn()).append(" = :").append(entityInfo.getIdColumn());

        List<Map<String, Object>> paramList = new ArrayList<>();
        for (X entity : entityList) {
            Map<String, Object> params = new HashMap<>();
            for (Map.Entry<String, Field> entry : allDeclareFields.entrySet()) {
                String columnName = columnMap.get(entry.getKey());
                if (columnName == null) continue;
                Field field = entry.getValue();
                try {
                    params.put(columnName, field.get(entity));
                } catch (IllegalAccessException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            paramList.add(params);
        }
        return batchUpdate(sql.toString(), paramList.toArray(new Map[entityList.size()]));
    }


    protected <X> StringBuilder convertUpdateGroup(UpdateGroup group, Map<String, Object> params, Class<X> clazz) {
        EntityInfo entityInfo = EntityHandle.getEntityInfo(clazz);
        List<UpdateNode> updateNodeList = group.getUpdateNodeList();
        StringBuilder sql = new StringBuilder(64);
        sql.append("UPDATE ").append(entityInfo.getTableName()).append(" SET ");
        for (UpdateNode updateNode : updateNodeList) {
            //append params
            sql.append(updateNode.getColumnName());
            if (updateNode.getValue() != null) {
                if (updateNode.getValue() instanceof Map) {
                    params.putAll((Map) updateNode.getValue());
                } else {
                    params.put(updateNode.getColumnName(), updateNode.getValue());
                    sql.append(" = :").append(updateNode.getColumnName());
                }
            }
            sql.append(',');
        }
        sql.setLength(sql.length() - 1);
        return sql;
    }

    private <X> X getUniqueResult(List<X> results) {
        if (results.isEmpty()) return null;
        if (results.size() > 1) {
            throw new IllegalTransactionStateException("result returned more than one element, returnedSize=" + results.size());
        }
        return results.get(0);
    }

    private <X> X getSingleResult(List<X> results) {
        if (results.size() != 1) {
            throw new IllegalTransactionStateException("result returned not one element, returnedSize=" + results.size());
        }
        return results.get(0);
    }


    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
}
