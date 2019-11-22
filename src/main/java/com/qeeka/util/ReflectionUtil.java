package com.qeeka.util;

import com.qeeka.SFunction;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by neal.xu on 2018/12/12.
 */
public class ReflectionUtil extends ReflectionUtils {
    //class field cache
    private static final Map<Class<?>, Map<String, Field>> FIELD_CACHE = new ConcurrentHashMap<>();
    //SerializedLambda cache
    private static final Map<Class<?>, WeakReference<SerializedLambda>> FUNC_CACHE = new ConcurrentHashMap<>();

    public static Map<String, Field> getAllDeclareFields(Class clazz) {
        //skip object class
        if (Object.class.equals(clazz)) {
            return null;
        }
        Map<String, Field> fieldMap = FIELD_CACHE.get(clazz);
        if (fieldMap != null) {
            return fieldMap;
        }
        synchronized (clazz) {
            //double check
            fieldMap = FIELD_CACHE.get(clazz);
            if (fieldMap != null) {
                return fieldMap;
            }
            fieldMap = new LinkedHashMap<>();
            Set<String> names = new HashSet<>();
            Class<?> searchType = clazz;
            while (!Object.class.equals(searchType) && searchType != null) {
                Field[] fields = searchType.getDeclaredFields();
                for (Field field : fields) {
                    if (names.contains(field.getName())) {
                        continue;
                    }
                    field.setAccessible(true);
                    fieldMap.put(field.getName(), field);
                }
                searchType = searchType.getSuperclass();
            }
            FIELD_CACHE.put(clazz, fieldMap);
            return fieldMap;
        }
    }

    public static <A extends Annotation> Field findUniqueFieldWithAnnotation(Class<?> clazz, final Class<A> type) {
        List<Field> fieldWithAnnotation = findFieldWithAnnotation(clazz, type);
        if (!fieldWithAnnotation.isEmpty()) return fieldWithAnnotation.get(0);
        return null;
    }

    public static <A extends Annotation> List<Field> findFieldWithAnnotation(Class<?> clazz, final Class<A> type) {
        final List<Field> fields = new ArrayList<>();
        Map<String, Field> allDeclareFields = getAllDeclareFields(clazz);
        for (Map.Entry<String, Field> entry : allDeclareFields.entrySet()) {
            if (entry.getValue().isAnnotationPresent(type)) {
                fields.add(entry.getValue());
            }
        }
        return fields;
    }

    //get function from cache or serialized
    public static <T> SerializedLambda resolve(SFunction<T, ?> fun) {
        Class<?> clazz = fun.getClass();
        //todo handle function cache & get
        return Optional.ofNullable(FUNC_CACHE.get(clazz))
                .map(WeakReference::get)
                .orElseGet(() -> {
                    SerializedLambda lambda = serialized(fun);
                    FUNC_CACHE.put(clazz, new WeakReference<>(lambda));
                    return lambda;
                });
    }

    private static <T> SerializedLambda serialized(SFunction<T, ?> fun) {
        try {
            Method replaceMethod = fun.getClass().getDeclaredMethod("writeReplace");
            replaceMethod.setAccessible(true);
            return (SerializedLambda) replaceMethod.invoke(fun);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
