package com.qeeka.test.repository;

import com.qeeka.repository.BaseSearchRepository;
import com.qeeka.test.domain.Person;
import org.springframework.stereotype.Repository;

/**
 * Created by Neal on 8/3 0003.
 */
@Repository
public class PersonRepository extends BaseSearchRepository<Person> {
}
