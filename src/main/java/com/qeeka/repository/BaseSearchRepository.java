package com.qeeka.repository;

import com.qeeka.domain.QueryModel;
import com.qeeka.domain.QueryParser;
import com.qeeka.http.QueryRequest;
import com.qeeka.http.QueryResponse;
import com.qeeka.operate.QueryResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;


/**
 * Created by Neal on 8/3 0003.
 */
public abstract class BaseSearchRepository<T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext
    protected EntityManager entityManager;
    //Get T  Real Class
    protected Class entityClass;
    protected String entityName;

    //Init query parse class
    private QueryParser queryParser = new QueryParser();

    /**
     * constructor and init class properties
     */
    public BaseSearchRepository() {
        Class<?>[] arguments = GenericTypeResolver.resolveTypeArguments(getClass(), BaseSearchRepository.class);
        if (arguments == null || arguments.length != 1) {
            throw new IllegalArgumentException(MessageFormatter.format("repository must extend with generic type like BaseSearchRepository<T>, class={}", getClass()).getMessage());
        }
        entityClass = arguments[0];

        String simpleName = entityClass.getSimpleName();
        Annotation annotation = entityClass.getAnnotation(Entity.class);
        if (annotation == null) {
            throw new IllegalArgumentException("repository must extend with generic type Entity.class");
        } else {
            if (annotation instanceof Entity) {
                Entity entity = (Entity) annotation;
                if (entity.name() != null && !"".equals(entity.name())) {
                    entityName = entity.name();
                } else {
                    entityName = simpleName;
                }
            } else {
                throw new IllegalArgumentException("repository must extend with generic type Entity");
            }
        }
    }

    /**
     * search
     *
     * @param queryRequest
     * @return
     */
    public QueryResponse<T> search(QueryRequest queryRequest) {
        //parse query group to simple query domain
        QueryModel query = queryParser.parse(queryRequest.getQueryGroup());
        StringBuilder hql = new StringBuilder("SELECT E FROM ").append(entityName).append(" AS E ");
        if (StringUtils.hasText(query.getStatement())) {
            hql.append(" WHERE ").append(query.getStatement());
        }
        if (StringUtils.hasText(query.getOrderStatement())) {
            hql.append(" ORDER BY ").append(query.getOrderStatement());
        }
        logger.debug("Generate HQL : {}", hql.toString());


        if (QueryResultType.LIST.equals(queryRequest.getQueryResultType())) {
            return listQuery(queryRequest, query, hql);
        } else {
            return singleQuery(query, hql, queryRequest.getQueryResultType());
        }
    }

    /**
     * query by query request
     *
     * @param query           1. QueryResultType.SINGLE : return single entity
     *                        2. QueryResultType.UNIQUE : must be return one
     * @param hql
     * @param queryResultType
     * @return response with single record
     */
    private QueryResponse<T> singleQuery(QueryModel query, StringBuilder hql, QueryResultType queryResultType) {
        QueryResponse<T> queryResponse = new QueryResponse<>();

        TypedQuery<T> recordQuery = entityManager.createQuery(hql.toString(), entityClass);
        for (Map.Entry<String, Object> entry : query.getParameters().entrySet()) {
            recordQuery.setParameter(entry.getKey(), entry.getValue());
        }
        recordQuery.setMaxResults(1);
        if (QueryResultType.SINGLE.equals(queryResultType)) {
            List<T> resultList = recordQuery.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                queryResponse.setEntity(resultList.get(0));
            }
        } else if (QueryResultType.UNIQUE.equals(queryResultType)) {
            queryResponse.setEntity(recordQuery.getSingleResult());
        }
        return queryResponse;
    }

    /**
     * query by query request
     *
     * @param queryRequest
     * @param query
     * @param hql
     * @return response with record list
     */
    private QueryResponse<T> listQuery(QueryRequest queryRequest, QueryModel query, StringBuilder hql) {
        QueryResponse<T> queryResponse = new QueryResponse<>();

        if (queryRequest.isNeedRecord()) {
            TypedQuery<T> recordQuery = entityManager.createQuery(hql.toString(), entityClass);
            for (Map.Entry<String, Object> entry : query.getParameters().entrySet()) {
                recordQuery.setParameter(entry.getKey(), entry.getValue());
            }
            //Page search , need page index and size
            if (queryRequest.getPageIndex() != null && queryRequest.getPageSize() != null) {
                if (queryRequest.getPageIndex() < 0) {
                    queryRequest.setPageIndex(0);
                }
                recordQuery.setFirstResult(queryRequest.getPageIndex() * queryRequest.getPageSize());
                recordQuery.setMaxResults(queryRequest.getPageSize());
            }
            //Set query record
            queryResponse.setRecords(recordQuery.getResultList());
        }
        //Query total
        if (queryRequest.isNeedCount()) {
            StringBuilder countHql = new StringBuilder("SELECT COUNT(E) FROM ").append(entityName).append(" E ");
            if (StringUtils.hasText(query.getStatement())) {
                countHql.append(" WHERE ").append(query.getStatement());
            }
            TypedQuery<Long> countQuery = entityManager.createQuery(countHql.toString(), Long.class);
            for (Map.Entry<String, Object> entry : query.getParameters().entrySet()) {
                countQuery.setParameter(entry.getKey(), entry.getValue());
            }
            Long total = countQuery.getSingleResult();
            //Set query total
            queryResponse.setTotalRecords(total);
        }
        return queryResponse;
    }

    /**
     * count by query request
     *
     * @param queryRequest
     * @return record total
     */
    public Long count(QueryRequest queryRequest) {
        QueryModel query = queryParser.parse(queryRequest.getQueryGroup());
        StringBuilder countHql = new StringBuilder("SELECT COUNT(E) FROM ").append(entityName).append(" E ");
        if (StringUtils.hasText(query.getStatement())) {
            countHql.append(" WHERE ").append(query.getStatement());
        }
        TypedQuery<Long> countQuery = entityManager.createQuery(countHql.toString(), Long.class);
        for (Map.Entry<String, Object> entry : query.getParameters().entrySet()) {
            countQuery.setParameter(entry.getKey(), entry.getValue());
        }
        return countQuery.getSingleResult();
    }

    /**
     * get entity by primary key
     *
     * @param id
     * @return
     */
    public T get(Object id) {
        StopWatch watch = new StopWatch();
        try {
            Object entity = entityManager.find(entityClass, id);
            if (entity != null) {
                return (T) entity;
            } else {
                return null;
            }
        } finally {
            logger.debug("get, entityClass={}, id={}, elapsedTime={}", entityClass.getName(), id, watch.elapsedTime());
        }
    }

    /**
     * get entity by entity class & id
     *
     * @param entityClass
     * @param id
     * @param <X>
     * @return
     */
    public <X> X get(Class<X> entityClass, Object id) {
        StopWatch watch = new StopWatch();
        try {
            return entityManager.find(entityClass, id);
        } finally {
            logger.debug("get, entityClass={}, id={}, elapsedTime={}", entityClass.getName(), id, watch.elapsedTime());
        }
    }

    /**
     * save entity
     *
     * @param entity
     */
    public void save(Object entity) {
        StopWatch watch = new StopWatch();
        try {
            entityManager.persist(entity);
        } finally {
            logger.debug("save, entityClass={}, elapsedTime={}", entity.getClass().getName(), watch.elapsedTime());
        }
    }

    /**
     * update entity
     *
     * @param entity
     */
    public void update(Object entity) {
        StopWatch watch = new StopWatch();
        try {
            entityManager.merge(entity);
        } finally {
            logger.debug("update, entityClass={}, elapsedTime={}", entity.getClass().getName(), watch.elapsedTime());
        }
    }

    /**
     * update by hql
     *
     * @param queryString
     * @return
     */
    public int update(String queryString) {
        return update(queryString, null);
    }

    /**
     * update by hql with params
     *
     * @param queryString
     * @param params
     * @return
     */
    public int update(CharSequence queryString, Map<String, Object> params) {
        StopWatch watch = new StopWatch();
        try {
            Query query = entityManager.createQuery(queryString.toString());
            if (params != null)
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            return query.executeUpdate();
        } finally {
            logger.debug("update, query={}, params={}, elapsedTime={}", queryString, params, watch.elapsedTime());
        }
    }

    /**
     * remove entity
     *
     * @param entity
     */
    public void delete(Object entity) {
        StopWatch watch = new StopWatch();
        try {
            entityManager.remove(entity);
        } finally {
            logger.debug("delete, entityClass={}, elapsedTime={}", entity.getClass().getName(), watch.elapsedTime());
        }
    }

    /**
     * delete by id
     */
    public void deleteById(Object id) {
        StopWatch watch = new StopWatch();
        try {
            T entity = get(id);
            if (entity != null) {
                entityManager.remove(entity);
            }
        } finally {
            logger.debug("delete, entityClass={},id={}, elapsedTime={}", entityClass.getName(), id, watch.elapsedTime());
        }
    }

    /**
     * refresh
     *
     * @param entity
     */
    public void refresh(Object entity) {
        StopWatch watch = new StopWatch();
        try {
            entityManager.refresh(entity);
        } finally {
            logger.debug("refresh, entityClass={}, elapsedTime={}", entity.getClass().getName(), watch.elapsedTime());
        }
    }

    /**
     * detach
     *
     * @param entity
     */
    public void detach(Object entity) {
        StopWatch watch = new StopWatch();
        try {
            entityManager.detach(entity);
        } finally {
            logger.debug("detach, entityClass={}, elapsedTime={}", entity.getClass().getName(), watch.elapsedTime());
        }
    }

    /**
     * Find by criteria
     *
     * @param query
     * @return
     */
    public List<T> find(CriteriaQuery query) {
        StopWatch watch = new StopWatch();
        try {
            return entityManager.createQuery(query).getResultList();
        } finally {
            logger.debug("find by CriteriaQuery<T>, elapsedTime={}", watch.elapsedTime());
        }
    }

    /**
     * Simple query
     *
     * @param queryString
     * @return
     */
    public List<T> find(CharSequence queryString) {
        return find(queryString, null);
    }

    /**
     * query with params
     *
     * @param queryString
     * @param params
     * @return
     */
    public List<T> find(CharSequence queryString, Map<String, Object> params) {
        StopWatch watch = new StopWatch();
        try {
            Query query = entityManager.createQuery(queryString.toString());
            if (params != null)
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            return query.getResultList();
        } finally {
            logger.debug("find, query={}, params={}, elapsedTime={}", queryString, params, watch.elapsedTime());
        }
    }

    /**
     * find by query String with paging
     *
     * @param queryString
     * @param offset
     * @param fetchSize
     * @return
     */
    public List<T> find(CharSequence queryString, int offset, int fetchSize) {
        return find(queryString, null, offset, fetchSize);
    }

    /**
     * find by query String with params&paging
     *
     * @param queryString
     * @param params
     * @param offset
     * @param fetchSize
     * @return
     */
    public List<T> find(CharSequence queryString, Map<String, Object> params, int offset, int fetchSize) {
        StopWatch watch = new StopWatch();
        try {
            Query query = entityManager.createQuery(queryString.toString());
            if (params != null) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
            query.setFirstResult(offset);
            query.setMaxResults(fetchSize);
            return query.getResultList();
        } finally {
            logger.debug("find, query={}, params={},offset={},fetchSize={}, elapsedTime={}", queryString, params, offset, fetchSize, watch.elapsedTime());
        }
    }

    /**
     * criteria query
     *
     * @param query
     * @param offset
     * @param fetchSize
     * @return
     */
    public List<T> find(CriteriaQuery<T> query, int offset, int fetchSize) {
        StopWatch watch = new StopWatch();
        try {
            TypedQuery<T> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult(offset);
            typedQuery.setMaxResults(fetchSize);
            return typedQuery.getResultList();
        } finally {
            logger.debug("find by CriteriaQuery<T>,offset={},fetchSize={}, elapsedTime={}", offset, fetchSize, watch.elapsedTime());
        }
    }

    /**
     * find unique by criteria query
     *
     * @param query
     * @return
     */
    public <X> X findUniqueResult(CriteriaQuery<X> query) {
        StopWatch watch = new StopWatch();
        try {
            List<X> results = entityManager.createQuery(query).getResultList();
            return getUniqueResult(results);
        } finally {
            logger.debug("findUniqueResult by CriteriaQuery<T>, elapsedTime={}", watch.elapsedTime());
        }
    }

    /**
     * find unique by query string
     *
     * @param queryString
     * @return
     */
    public <X> X findUniqueResult(CharSequence queryString) {
        return findUniqueResult(queryString, null);
    }

    /**
     * find unique by query string & params
     *
     * @param queryString
     * @param params
     * @return
     */
    public <X> X findUniqueResult(CharSequence queryString, Map<String, Object> params) {
        StopWatch watch = new StopWatch();
        try {
            Query query = entityManager.createQuery(queryString.toString());
            if (params != null) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
            List<X> results = query.getResultList();
            return getUniqueResult(results);
        } finally {
            logger.debug("findUniqueResult, query={}, params={}, elapsedTime={}", queryString, params, watch.elapsedTime());
        }
    }

    private <X> X getUniqueResult(List<X> results) {
        if (results.isEmpty()) return null;
        if (results.size() > 1) {
            throw new NonUniqueResultException("result returned more than one element, returnedSize=" + results.size());
        }
        return results.get(0);
    }

    //-------------------------------------------------------
    //                      SQL Query
    //-------------------------------------------------------

    /**
     * find list by native query
     *
     * @param sql
     * @return
     */
    public List<T> findByNativeQuery(CharSequence sql) {
        return findByNativeQuery(sql);
    }

    /**
     * find list by native query & params
     *
     * @param sql
     * @param params
     * @return
     */
    public List<T> findByNativeQuery(CharSequence sql, Map<String, Object> params) {
        return findByNativeQuery(sql, params);
    }

    /**
     * find list by native query & sql & offset & size
     *
     * @param sql
     * @param offset
     * @param size
     * @return
     */
    public List<T> findByNativeQuery(CharSequence sql, int offset, int size) {
        return findByNativeQuery(sql, offset, size);
    }

    /**
     * find list by native query & sql & params & offset & size
     *
     * @param sql
     * @param params
     * @param offset
     * @param size
     * @return
     */
    public List<T> findByNativeQuery(CharSequence sql, Map<String, Object> params, Integer offset, Integer size) {
        Query namedQuery = entityManager.createNativeQuery(sql.toString(), entityClass);
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                namedQuery.setParameter(entry.getKey(), entry.getValue());
            }
        }
        if (offset != null) {
            namedQuery.setFirstResult(offset);
        }
        if (size != null) {
            namedQuery.setMaxResults(size);
        }
        return namedQuery.getResultList();
    }

    /**
     * find unique by native query & sql
     *
     * @param sql
     * @param <X>
     * @return
     */
    public <X> X findUniqueNativeQuery(CharSequence sql) {
        return findUniqueNativeQuery(sql, null);
    }

    /**
     * find unique by native query & sql & params
     *
     * @param sql
     * @param params
     * @param <X>
     * @return
     */
    public <X> X findUniqueNativeQuery(CharSequence sql, Map<String, Object> params) {
        StopWatch watch = new StopWatch();
        try {
            Query query = entityManager.createNativeQuery(sql.toString());
            if (params != null) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
            List<X> results = query.getResultList();
            return getUniqueResult(results);
        } finally {
            logger.debug("findUniqueNativeResult, query={}, params={}, elapsedTime={}", sql, params, watch.elapsedTime());
        }
    }


    private final class StopWatch {
        private long start;

        public StopWatch() {
            reset();
        }

        public void reset() {
            start = System.currentTimeMillis();
        }

        public long elapsedTime() {
            long end = System.currentTimeMillis();
            return end - start;
        }
    }

}
