package com.qeeka.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by neal.xu on 2018/12/11.
 * Specifies column name for entity. default name is property name
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface Column {
    String value();
}
