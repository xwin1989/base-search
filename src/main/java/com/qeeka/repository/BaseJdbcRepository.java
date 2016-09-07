package com.qeeka.repository;

import com.qeeka.jdbc.BeanRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Created by Neal on 16/4/7.
 */
public abstract class BaseJdbcRepository<T> extends BaseSearchRepository<T> {

    protected NamedParameterJdbcTemplate jdbcTemplate;

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


    public <X> List<X> query(CharSequence sql, Map<String, Object> params) {
        return query(sql, params, null);
    }


    /**
     * find list by native query with sql & params & offset & size & entity class
     *
     * @param sql
     * @param params
     * @return list
     */
    public <X> List<X> query(CharSequence sql, Map<String, Object> params, Class<X> clazz) {
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
    public T queryUnique(CharSequence sql, Map<String, Object> params) {
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
    public <X> X queryUnique(CharSequence sql, Map<String, Object> params, Class<X> resultClass) {
        StopWatch watch = new StopWatch();
        try {
            List<X> resultList = jdbcTemplate.query(sql.toString(), params, BeanRowMapper.forClass(resultClass));
            return getUniqueResult(resultList);
        } finally {
            logger.debug("native unique query, query={}, params={}, elapsedTime={}", sql, params, watch.elapsedTime());
        }
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
    public List<Object> queryForList(final CharSequence sql, Map<String, Object> params) {
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
    public <X> List<X> queryForList(final CharSequence sql, Map<String, Object> params, Class<X> clazz) {
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
    public <X> X queryForObject(final CharSequence sql, Map<String, Object> params, Class<X> clazz) {
        StopWatch watch = new StopWatch();
        try {
            return jdbcTemplate.queryForObject(sql.toString(), params, clazz);
        } finally {
            logger.debug("queryForObject, query={}, params={}, resultSize={}, elapsedTime={}", sql, params, watch.elapsedTime());
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
    public Map<String, Object> queryForMap(final CharSequence sql, Map<String, Object> params) {
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
    public int updateNative(CharSequence sql, Map<String, Object> params) {
        StopWatch watch = new StopWatch();
        int size = 0;
        try {
            size = jdbcTemplate.update(sql.toString(), params);
            return size;
        } finally {
            logger.debug("native update, query={}, updateSize={}, elapsedTime={}", sql, size, watch.elapsedTime());
        }
    }

    public int[] batchUpdateNative(CharSequence sql, List<T> objects) {
        StopWatch watch = new StopWatch();
        SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(objects.toArray());
        try {
            return jdbcTemplate.batchUpdate(sql.toString(), params);
        } finally {
            logger.debug("native batch update, query={}, updateSize={}, elapsedTime={}", sql, objects.size(), watch.elapsedTime());
        }
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }
}
