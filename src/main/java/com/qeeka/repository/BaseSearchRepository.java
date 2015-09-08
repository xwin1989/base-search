package com.qeeka.repository;

import com.qeeka.domain.QueryModel;
import com.qeeka.domain.QueryParser;
import com.qeeka.domain.QueryRequest;
import com.qeeka.domain.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.lang.annotation.Annotation;
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

    public BaseSearchRepository() {
        Class<?>[] arguments = GenericTypeResolver.resolveTypeArguments(getClass(), BaseSearchRepository.class);
        if (arguments == null || arguments.length != 1) {
            throw new IllegalArgumentException(MessageFormatter.format("repository must extend with generic type like BaseSearchRepository<T>, class={}", getClass()).getMessage());
        }
        entityClass = arguments[0];

        String simpleName = entityClass.getSimpleName();
        System.out.println(simpleName);
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

    public QueryResponse<T> search(QueryRequest queryRequest) {
        //parse query group to simple query domain
        QueryModel query = queryParser.parse(queryRequest.getQueryGroup());

        StringBuilder hql = new StringBuilder("FROM ").append(entityName);
        if (StringUtils.hasText(query.getStatement())) {
            hql.append(" WHERE ").append(query.getStatement());
        }
        if (StringUtils.hasText(query.getOrderStatement())) {
            hql.append(" ORDER BY ").append(query.getOrderStatement());
        }
        logger.debug("Generate HQL : {}", hql.toString());


        QueryResponse<T> queryResponse = new QueryResponse<>();

        if (queryRequest.isNeedRecord()) {
            TypedQuery<T> recordQuery = entityManager.createQuery(hql.toString(), entityClass);
            for (Map.Entry<String, Object> entry : query.getParameters().entrySet()) {
                recordQuery.setParameter(entry.getKey(), entry.getValue());
            }
            //Page search , need page index and size
            if (queryRequest.getPageIndex() != null && queryRequest.getPageSize() != null) {
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
            queryResponse.setTotal(total);
        }
        return queryResponse;
    }
}
