package com.qeeka.test.repository;

import com.qeeka.repository.BaseJdbcRepository;
import com.qeeka.test.domain.UserEntity;
import org.springframework.stereotype.Service;

/**
 * Created by neal.xu on 2018/12/13.
 */
@Service
public class UserRepository extends BaseJdbcRepository<UserEntity> {
}
