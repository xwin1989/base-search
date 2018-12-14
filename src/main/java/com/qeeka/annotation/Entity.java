package com.qeeka.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by neal.xu on 2018/12/11.
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface Entity {
    /**
     * db table name
     *
     * @return
     */
    String table();

    /**
     * db schema, default null
     *
     * @return
     */
    String schema() default "";
}
