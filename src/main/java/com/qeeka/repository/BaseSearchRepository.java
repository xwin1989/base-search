package com.qeeka.repository;

import com.qeeka.domain.MapHandle;
import com.qeeka.domain.QueryGroup;
import com.qeeka.domain.QueryModel;
import com.qeeka.domain.QueryParser;
import com.qeeka.http.QueryRequest;
import com.qeeka.http.QueryResponse;
import com.qeeka.operate.QueryLinkOperate;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Neal on 8/3 0003.
 */
public abstract class BaseSearchRepository<T> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected EntityManager entityManager;
    //Get T  Real Class
    protected Class<T> entityClass;
    protected String entityName;

    //Init query parse class
    protected final QueryParser queryParser = new QueryParser();

    /**
     * constructor and init class properties
     */
    public BaseSearchRepository() {
        Class<?>[] arguments = GenericTypeResolver.resolveTypeArguments(getClass(), BaseSearchRepository.class);
        if (arguments == null || arguments.length != 1) {
            throw new IllegalArgumentException(MessageFormatter.format("repository must extend with generic type like BaseSearchRepository<T>, class={}", getClass()).getMessage());
        }
        entityClass = (Class<T>) arguments[0];

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
     * Search by default query request
     *
     * @return
     */
    public QueryResponse<T> search() {
        return search(new QueryRequest(new QueryGroup()));
    }

    /**
     * Search by default query group
     *
     * @param queryGroup
     * @return
     */
    public QueryResponse<T> search(QueryGroup queryGroup) {
        return search(new QueryRequest(queryGroup));
    }

    /**
     * Search by unique query group
     *
     * @param queryGroup
     * @return
     */
    public QueryResponse<T> searchUnique(QueryGroup queryGroup) {
        return search(new QueryRequest(queryGroup).uniqueResult());
    }

    /**
     * Search by unique query request
     *
     * @param queryRequest
     * @return
     */
    public QueryResponse<T> searchUnique(QueryRequest queryRequest) {
        return search(queryRequest.uniqueResult());
    }

    /**
     * Search by single query group
     *
     * @param queryGroup
     * @return
     */
    public QueryResponse<T> searchSingle(QueryGroup queryGroup) {
        return search(new QueryRequest(queryGroup).singleResult());
    }

    /**
     * Search by single query request
     *
     * @param queryRequest
     * @return
     */
    public QueryResponse<T> searchSingle(QueryRequest queryRequest) {
        return search(queryRequest.singleResult());
    }

    /**
     * search by query request
     *
     * @param queryRequest
     * @return
     */
    public QueryResponse<T> search(QueryRequest queryRequest) {
        StopWatch watch = new StopWatch();
        //parse query group to simple query domain
        QueryModel query = queryParser.parse(queryRequest.getQueryGroup());
        StringBuilder hql = new StringBuilder("SELECT ");
        if (queryRequest.isNeedDistinct()) {
            hql.append("DISTINCT(E)");
        } else {
            hql.append('E');
        }
        hql.append(" FROM ").append(entityName).append(" AS E ");
        if (queryRequest.getQueryGroup() != null && queryRequest.getQueryGroup().getEntityMapping() != null) {
            for (Map.Entry<String, Map<QueryLinkOperate, String>> joinMap : queryRequest.getQueryGroup().getEntityMapping().entrySet()) {
                for (Map.Entry<QueryLinkOperate, String> entry : joinMap.getValue().entrySet()) {
                    if (entry.getKey().equals(QueryLinkOperate.CROSS_JOIN)) {
                        hql.append(" , ").append(joinMap.getKey()).append(" AS ").append(entry.getValue()).append(' ');
                    } else {
                        hql.append(entry.getKey().getValue());
                        if (entry.getKey().isNeedFetch()) {
                            hql.append(" FETCH ");
                        }
                        hql.append(joinMap.getKey()).append(" AS ").append(entry.getValue());
                    }
                }
            }
        }
        if (StringUtils.hasText(query.getStatement())) {
            hql.append(" WHERE ").append(query.getStatement());
        }
        if (StringUtils.hasText(query.getOrderStatement())) {
            hql.append(" ORDER BY ").append(query.getOrderStatement());
        }
        try {
            if (QueryResultType.LIST.equals(queryRequest.getQueryResultType())) {
                return listQuery(queryRequest, query, hql);
            } else {
                return singleQuery(query, hql, queryRequest.getQueryResultType());
            }
        } finally {
            logger.debug("search, query={}, pageIndex={}, pageSize={}, elapsedTime={}", hql, queryRequest.getPageIndex(), queryRequest.getPageSize(), watch.elapsedTime());
        }
    }

    /**
     * query by query request
     *
     * @param query           1. QueryResultType.UNIQUE : return unique entity, one or null
     *                        2. QueryResultType.SINGLE : must be return one
     * @param hql
     * @param queryResultType
     * @return response with single record
     */
    private QueryResponse<T> singleQuery(QueryModel query, StringBuilder hql, QueryResultType queryResultType) {
        QueryResponse<T> queryResponse = new QueryResponse<>();

        TypedQuery<T> recordQuery = entityManager.createQuery(hql.toString(), entityClass);
        for (Map.Entry<String, ?> entry : query.getParameters().entrySet()) {
            recordQuery.setParameter(entry.getKey(), entry.getValue());
        }
        recordQuery.setMaxResults(1);
        if (QueryResultType.UNIQUE.equals(queryResultType)) {
            List<T> resultList = recordQuery.getResultList();
            if (resultList != null && !resultList.isEmpty()) {
                queryResponse.setEntity(resultList.get(0));
            }
        } else if (QueryResultType.SINGLE.equals(queryResultType)) {
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
        //Query record
        if (queryRequest.isNeedRecord()) {
            TypedQuery<T> recordQuery = entityManager.createQuery(hql.toString(), entityClass);
            for (Map.Entry<String, ?> entry : query.getParameters().entrySet()) {
                recordQuery.setParameter(entry.getKey(), entry.getValue());
            }
            //Page search , need page index and size
            if (queryRequest.getPageIndex() != null && queryRequest.getPageSize() != null) {
                //add index&size check
                if (queryRequest.getPageIndex() < 0) {
                    queryRequest.setPageIndex(0);
                }
                if (queryRequest.getPageSize() < 0) {
                    queryRequest.setPageSize(0);
                }
                if (queryRequest.getPageIndex() != 0)
                    recordQuery.setFirstResult(queryRequest.getPageIndex() * queryRequest.getPageSize());
                if (queryRequest.getPageSize() != 0)
                    recordQuery.setMaxResults(queryRequest.getPageSize());
            }
            //Set query record
            queryResponse.setRecords(recordQuery.getResultList());
            queryResponse.setPageIndex(queryRequest.getPageIndex());
            queryResponse.setPageSize(queryRequest.getPageSize());
        }
        //Query count
        if (queryRequest.isNeedCount()) {
            StringBuilder countHql = new StringBuilder("SELECT ");
            if (queryRequest.isNeedDistinct()) {
                countHql.append("COUNT(DISTINCT E)");
            } else {
                countHql.append("COUNT(E)");
            }
            countHql.append(" FROM ").append(entityName).append(" AS E ");
            if (queryRequest.getQueryGroup() != null && queryRequest.getQueryGroup().getEntityMapping() != null) {
                for (Map.Entry<String, Map<QueryLinkOperate, String>> joinMap : queryRequest.getQueryGroup().getEntityMapping().entrySet()) {
                    for (Map.Entry<QueryLinkOperate, String> entry : joinMap.getValue().entrySet()) {
                        if (entry.getKey().equals(QueryLinkOperate.CROSS_JOIN)) {
                            countHql.append(" , ").append(joinMap.getKey()).append(" AS ").append(entry.getValue()).append(' ');
                        } else {
                            countHql.append(entry.getKey().getValue()).append(joinMap.getKey()).append(" AS ").append(entry.getValue());
                        }
                    }
                }
            }
            if (StringUtils.hasText(query.getStatement())) {
                countHql.append(" WHERE ").append(query.getStatement());
            }
            TypedQuery<Long> countQuery = entityManager.createQuery(countHql.toString(), Long.class);
            for (Map.Entry<String, ?> entry : query.getParameters().entrySet()) {
                countQuery.setParameter(entry.getKey(), entry.getValue());
            }
            Long total = countQuery.getSingleResult();
            //Set query total
            queryResponse.setTotalRecords(total);
        }
        return queryResponse;
    }

    /**
     * count all elements
     *
     * @return
     */
    public Long count() {
        return count(new QueryGroup());
    }

    /**
     * * count elements by query request
     *
     * @param queryRequest
     * @return
     */
    public Long count(QueryRequest queryRequest) {
        return count(queryRequest.getQueryGroup());
    }

    /**
     * count by query group
     *
     * @param queryGroup
     * @return record total
     */
    public Long count(QueryGroup queryGroup) {
        QueryModel query = queryParser.parse(queryGroup);
        StringBuilder countHql = new StringBuilder("SELECT COUNT(E) FROM ").append(entityName).append(" E ");
        if (StringUtils.hasText(query.getStatement())) {
            countHql.append(" WHERE ").append(query.getStatement());
        }
        TypedQuery<Long> countQuery = entityManager.createQuery(countHql.toString(), Long.class);
        for (Map.Entry<String, ?> entry : query.getParameters().entrySet()) {
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
    public int update(CharSequence queryString) {
        return update(queryString, null);
    }

    /**
     * update by hql with params
     *
     * @param queryString
     * @param params
     * @return
     */
    public int update(CharSequence queryString, Map<String, ?> params) {
        StopWatch watch = new StopWatch();
        try {
            Query query = entityManager.createQuery(queryString.toString());
            if (params != null)
                for (Map.Entry<String, ?> entry : params.entrySet()) {
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
            logger.debug("delete, entityClass={}, id={}, elapsedTime={}", entityClass.getName(), id, watch.elapsedTime());
        }
    }

    /**
     * Simple query
     *
     * @param queryString
     * @return
     */
    public <X> List<X> find(CharSequence queryString) {
        return find(queryString, null, null, null);
    }

    /**
     * query with params
     *
     * @param queryString
     * @param params
     * @return
     */
    public <X> List<X> find(CharSequence queryString, Map<String, ?> params) {
        return find(queryString, params, null, null);
    }

    /**
     * find by query String with paging
     *
     * @param queryString
     * @param offset
     * @param fetchSize
     * @return
     */
    public <X> List<X> find(CharSequence queryString, Integer offset, Integer fetchSize) {
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
    public <X> List<X> find(CharSequence queryString, Map<String, ?> params, Integer offset, Integer fetchSize) {
        StopWatch watch = new StopWatch();
        try {
            Query query = entityManager.createQuery(queryString.toString());
            if (params != null) {
                for (Map.Entry<String, ?> entry : params.entrySet()) {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
            if (offset != null) {
                query.setFirstResult(offset);
            }
            if (fetchSize != null) {
                query.setMaxResults(fetchSize);
            }
            return query.getResultList();
        } finally {
            logger.debug("find, query={}, params={},offset={},fetchSize={}, elapsedTime={}", queryString, params, offset, fetchSize, watch.elapsedTime());
        }
    }

    /**
     * Find by criteria
     *
     * @param query
     * @return
     */
    public <X> List<X> find(CriteriaQuery query) {
        return find(query, null, null);
    }

    /**
     * criteria query
     *
     * @param query
     * @param offset
     * @param fetchSize
     * @return
     */
    public <X> List<X> find(CriteriaQuery<X> query, Integer offset, Integer fetchSize) {
        StopWatch watch = new StopWatch();
        try {
            TypedQuery<X> typedQuery = entityManager.createQuery(query);
            if (offset != null) {
                typedQuery.setFirstResult(offset);
            }
            if (fetchSize != null) {
                typedQuery.setMaxResults(fetchSize);
            }
            return typedQuery.getResultList();
        } finally {
            logger.debug("find by CriteriaQuery<T>, offset={}, fetchSize={}, elapsedTime={}", offset, fetchSize, watch.elapsedTime());
        }
    }

    /**
     * find unique by criteria query
     *
     * @param query
     * @return
     */
    @Deprecated
    public <X> X uniqueResult(CriteriaQuery<X> query) {
        StopWatch watch = new StopWatch();
        try {
            List<X> results = entityManager.createQuery(query).getResultList();
            return getUniqueResult(results);
        } finally {
            logger.debug("uniqueResult by CriteriaQuery<T>, elapsedTime={}", watch.elapsedTime());
        }
    }

    /**
     * find unique by query string
     *
     * @param queryString
     * @return
     */
    @Deprecated
    public <X> X uniqueResult(CharSequence queryString) {
        return uniqueResult(queryString, null);
    }

    /**
     * find unique by query string & params
     *
     * @param queryString
     * @param params
     * @return
     */
    @Deprecated
    public <X> X uniqueResult(CharSequence queryString, Map<String, ?> params) {
        return findUnique(queryString, params);
    }

    /**
     * find unique by query string
     *
     * @param queryString
     * @return
     */
    public <X> X findUnique(CharSequence queryString) {
        return findUnique(queryString, null);
    }

    /**
     * find unique by query string & params
     *
     * @param queryString
     * @param params
     * @return
     */
    public <X> X findUnique(CharSequence queryString, Map<String, ?> params) {
        StopWatch watch = new StopWatch();
        try {
            Query query = entityManager.createQuery(queryString.toString());
            if (params != null) {
                for (Map.Entry<String, ?> entry : params.entrySet()) {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
            query.setMaxResults(1);
            List<X> results = query.getResultList();
            return getUniqueResult(results);
        } finally {
            logger.debug("find unique, query={}, params={}, elapsedTime={}", queryString, params, watch.elapsedTime());
        }
    }


    protected <X> X getUniqueResult(List<X> results) {
        if (results.isEmpty()) return null;
        if (results.size() > 1) {
            throw new NonUniqueResultException("result returned more than one element, returnedSize=" + results.size());
        }
        return results.get(0);
    }

    /**
     * simple query
     *
     * @param queryString
     * @return a map
     */
    public Map<Object, T> findToMap(CharSequence queryString) {
        return findToMap(queryString, null, null, null);
    }

    /**
     * query with params
     *
     * @param queryString
     * @param params
     * @return a map
     */
    public Map<Object, T> findToMap(CharSequence queryString, Map<String, ?> params) {
        return findToMap(queryString, params, null, null);
    }

    /**
     * find by query String with paging
     *
     * @param queryString
     * @param offset
     * @param fetchSize
     * @return a map
     */
    public Map<Object, T> findToMap(CharSequence queryString, Integer offset, Integer fetchSize) {
        return findToMap(queryString, null, offset, fetchSize);
    }

    /**
     * find by query String with paging&param
     *
     * @param queryString
     * @param params
     * @param offset
     * @param fetchSize
     * @return a map
     */
    public Map<Object, T> findToMap(CharSequence queryString, Map<String, ?> params, Integer offset, Integer fetchSize) {
        List<T> results = find(queryString, params, offset, fetchSize);
        return getObjectMap(results);
    }

    /**
     * Find by criteria
     *
     * @param query
     * @return a map
     */
    public Map<Object, T> findToMap(CriteriaQuery query) {
        return findToMap(query, null, null);
    }

    /**
     * criteria query
     *
     * @param query
     * @param offset
     * @param fetchSize
     * @return a map
     */
    public Map<Object, T> findToMap(CriteriaQuery<T> query, Integer offset, Integer fetchSize) {
        List<T> results = find(query, offset, fetchSize);
        return getObjectMap(results);
    }

    /**
     * convert list to map
     *
     * @param results
     * @return map
     */
    protected Map<Object, T> getObjectMap(List<T> results) {
        Map<Object, T> recordMap = new HashMap<>();
        if (!results.isEmpty() && results.get(0) instanceof MapHandle) {
            for (T result : results) {
                recordMap.put(((MapHandle) result).getPrimaryKey(), result);
            }
        }
        return recordMap;
    }

    /**
     * batch update
     *
     * @param entityList
     */
    public List<T> batchUpdate(List<T> entityList) {
        StopWatch watch = new StopWatch();
        try {
            for (T t : entityList) {
                update(t);
            }
        } finally {
            logger.debug("update batch, size={}, elapsedTime={}", entityList.size(), watch.elapsedTime());
        }
        entityManager.flush();
        entityManager.clear();
        return entityList;
    }

    /**
     * batch save
     *
     * @param entityList
     */
    public List<T> batchSave(List<T> entityList) {
        StopWatch watch = new StopWatch();
        try {
            for (T t : entityList) {
                save(t);
            }
            entityManager.flush();
            entityManager.clear();
        } finally {
            logger.debug("save batch, size={}, elapsedTime={}", entityList.size(), watch.elapsedTime());
        }
        return entityList;
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
     * Flush entity manager
     */
    public void flush() {
        StopWatch watch = new StopWatch();
        try {
            entityManager.flush();
        } finally {
            logger.debug("flush, elapsedTime={}", watch.elapsedTime());
        }
    }

    protected final class StopWatch {

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

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
