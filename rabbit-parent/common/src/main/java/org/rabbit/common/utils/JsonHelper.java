package org.rabbit.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Json format tool
 */
public abstract class JsonHelper {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        // ignore null
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, true);
    }

    /**
     * Convert json string to object
     *
     * @param content   String of waiting convert
     * @param valueType The object Type
     *                  Notice：Types with generics cannot be parsed because of Java's generic erasure mechanism，such as collection
     *                  type (Map,List,Set) , you shoule be use the method {@link #read(String, TypeReference)}
     */
    public static <T> T read(String content, Class<T> valueType) {
        try {
            return mapper.readValue(content, valueType);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * Convert json string to object
     *
     * @param content   String of waiting convert
     * @param typeReference The TypeReference of Object
     *
     */
    public static <T> T read(String content, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(content, typeReference);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * Convert Object to json string
     * @param value Object
     */
    public static String write(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

}
