package com.qeeka.test.repository;

import com.qeeka.repository.BaseJdbcRepository;
import com.qeeka.test.domain.Company;
import org.springframework.stereotype.Repository;

/**
 * Created by Neal on 16/4/8.
 */
@Repository
public class CompanyRepository extends BaseJdbcRepository<Company> {
}
