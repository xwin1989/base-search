package com.qeeka.annotation;

import com.qeeka.enums.GenerationType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by neal.xu on 2018/12/11.
 * Specifies the primary key of an entity.
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface Id {
    GenerationType strategy() default GenerationType.IDENTITY;
}
