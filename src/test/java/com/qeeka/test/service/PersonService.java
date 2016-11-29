package com.qeeka.test.service;

import com.qeeka.domain.QueryGroup;
import com.qeeka.http.QueryRequest;
import com.qeeka.http.QueryResponse;
import com.qeeka.test.domain.Person;
import com.qeeka.test.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Neal on 2015/7/27.
 */
@Service
@Transactional
public class PersonService {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PersonRepository repository;


    public void remove(int personId) {
        Person person = entityManager.find(Person.class, personId);
        this.entityManager.remove(person);
    }

    public QueryResponse<Person> query(QueryRequest request) {
        return repository.query(request);
    }

    public QueryResponse<Person> search(QueryRequest request) {
        return repository.search(request);
    }

    public Long count(QueryRequest request) {
        return repository.count(request);
    }

    public Long count(QueryGroup queryGroup) {
        return repository.count(queryGroup);
    }
}
