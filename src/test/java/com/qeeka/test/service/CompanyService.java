package com.qeeka.test.service;

import com.qeeka.test.domain.Company;
import com.qeeka.test.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Neal on 16/4/8.
 */
@Service
public class CompanyService {
    private CompanyRepository repository;

    @Transactional
    public int updateNative(String sql) {
        return repository.update(sql);
    }

    @Transactional
    public int updateNative(String sql, Map<String, Object> params) {
        return repository.update(sql, params);
    }

    public List<Company> query(String sql) {
        return repository.query(sql);
    }

    public <T> List<T> query(String sql, Class<T> clazz) {
        return repository.query(sql, clazz);
    }

    public Company queryUnique(String sql) {
        return repository.queryUnique(sql);
    }

    public Company queryUnique(String sql, Map<String, Object> params) {
        return repository.queryUnique(sql, params);
    }

    public <T> T queryUnique(String sql, Class<T> clazz) {
        return repository.queryUnique(sql, clazz);
    }

    public <T> T queryUnique(String sql, Map<String, Object> params, Class<T> clazz) {
        return repository.queryUnique(sql, params, clazz);
    }


    public <T> List<T> queryList(String sql, Class<T> clazz) {
        return repository.queryForList(sql, clazz);
    }

    public <T> List<T> queryList(String sql, Map<String, Object> params, Class<T> clazz) {
        return repository.queryForList(sql, params, clazz);
    }


    public <T> T queryForObject(String sql, Class<T> clazz) {
        return repository.queryForObject(sql, clazz);
    }

    public <T> T queryForObject(String sql, Map<String, Object> params, Class<T> clazz) {
        return repository.queryForObject(sql, params, clazz);
    }


    public Map<String, Object> queryForMap(String sql) {
        return repository.queryForMap(sql);
    }

    public Map<String, Object> queryForMap(String sql, Map<String, Object> params) {
        return repository.queryForMap(sql, params);
    }

    public List<Company> queryWithRowMap(String sql, Map<String, Object> params) {
        return repository.query(sql, params, new RowMapper<Company>() {
            @Override
            public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
                Company company = new Company();
                company.setId(rs.getLong("id"));
                company.setName(rs.getString("name"));
                return company;
            }
        });
    }

    public List<Company> queryWithRowMap(String sql) {
        return repository.query(sql, new RowMapper<Company>() {
            @Override
            public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
                Company company = new Company();
                company.setId(rs.getLong("id"));
                company.setName(rs.getString("name"));
                return company;
            }
        });
    }

    public Company save(Company company) {
        return repository.save(company);
    }

    @Autowired
    public void setCompanyRepository(CompanyRepository companyRepository) {
        this.repository = companyRepository;
    }
}
