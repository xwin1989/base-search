package com.qeeka;

import java.io.Serializable;
import java.util.function.Function;

/**
 * Created by neal.xu on 2019/11/21.
 */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}
