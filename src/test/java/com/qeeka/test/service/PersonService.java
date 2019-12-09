package com.qeeka.test.service;

import com.qeeka.domain.QueryGroup;
import com.qeeka.domain.QueryResponse;
import com.qeeka.domain.UpdateGroup;
import com.qeeka.test.domain.Person;
import com.qeeka.test.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Neal on 2015/7/27.
 */
@Service
@Transactional
public class PersonService {

    @Autowired
    private PersonRepository repository;

    public Person get(Integer id) {
        return repository.get(id);
    }

    public int delete(int personId) {
        Person person = repository.get(personId, Person.class);
        return repository.delete(person);
    }

    public int delete2(int persionId) {
        return repository.delete(new QueryGroup("id", persionId));
    }


    public int deleteById(Integer id) {
        return repository.deleteById(id);
    }

    public QueryResponse<Person> search(QueryGroup group) {
//        return repository.query(group);
        return null;}

    public Long count(QueryGroup queryGroup) {
        return repository.count(queryGroup);
    }

    public Integer update(UpdateGroup group) {
        return repository.update(group);
    }

    public int update(String sql) {
        return repository.update(sql);
    }
}
