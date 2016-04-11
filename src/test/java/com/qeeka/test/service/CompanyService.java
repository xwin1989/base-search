package com.qeeka.test.service;

import com.qeeka.test.domain.Company;
import com.qeeka.test.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return repository.updateNative(sql);
    }

    @Transactional
    public int updateNative(String sql, Map<String, Object> params) {
        return repository.updateNative(sql, params);
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


    public List<Object> queryList(String sql) {
        return repository.queryForList(sql);
    }

    public List<Object> queryList(String sql, Map<String, Object> params) {
        return repository.queryForList(sql, params);
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


    @Autowired
    public void setCompanyRepository(CompanyRepository companyRepository) {
        this.repository = companyRepository;
    }
}
