package com.qeeka.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.qeeka.deserializer.QueryGroupJsonDeserializer;
import com.qeeka.domain.QueryGroup;
import com.qeeka.http.QueryRequest;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class QueryJSONBinder {
    static final ObjectMapper DEFAULT_OBJECT_MAPPER;

    static {
        DEFAULT_OBJECT_MAPPER = createMapper();
    }

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        mapper.setDateFormat(dateFormat);
        mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector(TypeFactory.defaultInstance()));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.configure(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME, true);

        addQueryMapper(mapper);
        return mapper;
    }

    /**
     * Add Query Deserializer to object mapper
     *
     * @param objectMapper
     * @return
     */
    public static ObjectMapper addQueryMapper(ObjectMapper objectMapper) {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(QueryGroup.class, new QueryGroupJsonDeserializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }

    public static QueryRequest fromJSON(String json) {
        try {
            return DEFAULT_OBJECT_MAPPER.readValue(json, QueryRequest.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJSON(QueryRequest request) {
        try {
            return DEFAULT_OBJECT_MAPPER.writeValueAsString(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
