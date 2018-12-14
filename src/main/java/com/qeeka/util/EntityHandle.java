package com.qeeka.util;

import com.qeeka.annotation.Column;
import com.qeeka.annotation.Entity;
import com.qeeka.annotation.Id;
import com.qeeka.annotation.Transient;
import com.qeeka.domain.EntityInfo;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by neal.xu on 2018/12/12.
 */
public class EntityHandle {
    private static final Map<Class<?>, EntityInfo> INFO_CACHE = new ConcurrentHashMap<>();

    public static EntityInfo getEntityInfo(Class clazz) {
        EntityInfo info = INFO_CACHE.get(clazz);
        if (info != null) {
            return info;
        }
        synchronized (clazz) {
            info = INFO_CACHE.get(clazz);
            if (info != null) {
                return info;
            }
            EntityInfo entityInfo = new EntityInfo();
            Annotation annotation = clazz.getAnnotation(Entity.class);
            if (annotation == null) {
                throw new IllegalArgumentException("repository must extend with generic type @Entity");
            } else {
                String tName = clazz.getSimpleName();
                if (annotation instanceof Entity) {
                    Entity entity = (Entity) annotation;
                    if (StringUtils.hasText(entity.table())) {
                        tName = entity.table();
                    }
                    if (StringUtils.hasText(entity.schema())) {
                        tName = String.format("%s.%s", entity.schema(), entity.table());
                    }
                } else {
                    throw new IllegalArgumentException("repository must extend with generic type Entity");
                }
                entityInfo.setTableName(tName);
            }


            //process entity annotation, generate target id & column
            String idNames = null;
            Map<String, Field> allDeclareFields = ReflectionUtil.getAllDeclareFields(clazz);
            for (Map.Entry<String, Field> entry : allDeclareFields.entrySet()) {
                Field field = entry.getValue();
                //skip transient
                if (field.isAnnotationPresent(Transient.class)) continue;
                //get column name
                String columnName = field.getName();
                if (field.isAnnotationPresent(Column.class)) {
                    columnName = field.getAnnotation(Column.class).value();
                }
                if (field.isAnnotationPresent(Id.class)) {
                    if (idNames != null) {
                        throw new IllegalArgumentException("Current only support single @Id annotation");
                    }
                    idNames = columnName;
                    entityInfo.setStrategy(field.getAnnotation(Id.class).strategy());
                }
                entityInfo.getColumnMap().put(field.getName(), columnName);
            }
            entityInfo.setIdColumn(idNames);
            entityInfo.setClazz(clazz);
            INFO_CACHE.put(clazz, entityInfo);
            return entityInfo;
        }
    }

    public static CharSequence convertColumnMapping(CharSequence[] fields, EntityInfo entityInfo) {
        if (fields == null || fields.length == 0) {
            return null;
        }
        StringBuilder columns = new StringBuilder();
        for (CharSequence field : fields) {
            String column = entityInfo.getColumnMap().get(field);
            if (column == null)
                throw new IllegalArgumentException(String.format("Entity #%s column %s can't mapping!", entityInfo.getClazz().getName(), field));
            columns.append(column).append(" AS ").append(field).append(',');
        }
        columns.setLength(columns.length() - 1);
        return columns;
    }

    public static CharSequence convertColumnMapping(EntityInfo entityInfo, boolean appendMaster) {
        StringBuilder columnNames = new StringBuilder(64);
        for (Map.Entry<String, String> entry : entityInfo.getColumnMap().entrySet()) {
            if (appendMaster) columnNames.append("E.");
            columnNames.append(entry.getValue()).append(" AS ").append(entry.getKey()).append(',');
        }
        columnNames.setLength(columnNames.length() - 1);
        return columnNames;
    }

    public static CharSequence convertColumn(EntityInfo entityInfo, boolean appendMaster) {
        StringBuilder columnNames = new StringBuilder(64);
        for (Map.Entry<String, String> entry : entityInfo.getColumnMap().entrySet()) {
            if (appendMaster) columnNames.append("E.");
            columnNames.append(entry.getValue()).append(',');
        }
        columnNames.setLength(columnNames.length() - 1);
        return columnNames;
    }


}
