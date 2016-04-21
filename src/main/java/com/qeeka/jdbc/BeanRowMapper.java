package com.qeeka.jdbc;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Neal on 16/4/6.
 */
public final class BeanRowMapper<T> implements RowMapper<T> {
    private final Class<T> clazz;
    private final Map<String, Field> columnMappings = new HashMap<>();
    private Map<Integer, Field> resultSetFieldMappings;

    private BeanRowMapper(Class<T> entityClass) {
        clazz = entityClass;
        initializeColumnMappings(entityClass);
    }

    public static <T> BeanRowMapper<T> forClass(Class<T> entityClass) {
        return new BeanRowMapper<>(entityClass);
    }

    @Override
    public T mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        if (resultSetFieldMappings == null) {
            resultSetFieldMappings = buildResultSetFieldMappings(resultSet);
        }
        try {
            T result = BeanUtils.instantiate(clazz);
            assignColumnValues(resultSet, result, resultSetFieldMappings);
            return result;
        } catch (IllegalAccessException e) {
            throw new SQLException("failed to create instance, class=" + clazz, e);
        }
    }

    private Map<Integer, Field> buildResultSetFieldMappings(ResultSet resultSet) throws SQLException {
        Map<Integer, Field> resultSetFieldMappings = new HashMap<>();
        ResultSetMetaData meta = resultSet.getMetaData();
        int count = meta.getColumnCount();
        for (int i = 1; i < count + 1; i++) {
            String column = meta.getColumnLabel(i);
            Field field = columnMappings.get(column.toLowerCase());
            if (field != null) {
                resultSetFieldMappings.put(i, field);
            }
        }

        return resultSetFieldMappings;
    }

    /**
     * init field mapping
     *
     * @param entityClass
     */
    private void initializeColumnMappings(Class<T> entityClass) {
        Class<?> targetClass = entityClass;
        while (!Object.class.equals(targetClass)) {
            Field[] fields = targetClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    String columnName = column.name().toLowerCase();
                    columnMappings.put(columnName, field);
                } else {
                    String fieldName = field.getName().toLowerCase();
                    columnMappings.put(fieldName, field);
                }
            }
            targetClass = targetClass.getSuperclass();
        }
    }

    private void assignColumnValues(ResultSet resultSet, T result, Map<Integer, Field> resultSetFieldMappings) throws SQLException, IllegalAccessException {
        for (Map.Entry<Integer, Field> entry : resultSetFieldMappings.entrySet()) {
            Field field = entry.getValue();
            Object value = JdbcUtils.getResultSetValue(resultSet, entry.getKey(), field.getType());
            field.set(result, value);
        }
    }
}
