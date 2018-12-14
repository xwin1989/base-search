package com.qeeka.test.repository;

import com.qeeka.repository.BaseJdbcRepository;
import com.qeeka.test.domain.Book;
import org.springframework.stereotype.Repository;

/**
 * Created by Neal on 8/3 0003.
 */
@Repository
public class BookRepository extends BaseJdbcRepository<Book> {
}
