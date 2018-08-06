package com.arcvideo.pgcliveplatformserver.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JsonUtils {

    private static final Logger log = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final String OP_FORMAT = "{\"r\":%1$d}";

    private static final byte OP_SUCCESS = 1;

    private static final byte OP_FAILED = 0;

    public static final String KEY_OP_RESULT = "r";

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
    }

    private JsonUtils() {
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }


    /**
     * Converts the given data to an json-style string or returns the defaultJson if failed to
     * do converts.
     *
     * @param data        the data to be converted
     * @param defaultJson the default json string if failed to do convert
     * @return the json-style string
     */
    public static String toJson(Object data, String defaultJson) {
        if (data != null) {
            try {
                return mapper.writeValueAsString(data);
            } catch (JsonProcessingException e) {
                log.error("", e);
            }
        }
        return defaultJson;
    }

    /**
     * Construct a json string indicates that the status of operation is succeed or failed.
     *
     * @param isSuccess true if the operation is successful
     * @return the json string format from {@link #OP_FORMAT}
     */
    public static String status(boolean isSuccess) {
        return String.format(OP_FORMAT, isSuccess ? OP_SUCCESS : OP_FAILED);
    }

    /**
     * Converts the given data to an json-style string or returns an failed status with {@link #status}.
     *
     * @param data the data to be converted
     * @return the json-style string convert from data or if failed to do convert it
     * will returns an failed status with {@link #status}
     */
    public static String toJsonOrStatus(Object data) {
        return toJson(data, String.format(OP_FORMAT, 0));
    }

    /**
     * Converts the given data to an json-style string.
     *
     * @param data the data to be converted
     * @return the json-style string convert from data or {@code null} if failed to do convert
     */
    public static String toJsonOrNull(Object data) {
        return toJson(data, null);
    }

    /**
     * Converts teh given data to an json-style string or an empty string if failed to do convert.
     *
     * @param data the data to be converted
     * @return the json-style string
     */
    public static String toJsonOrEmpty(Object data) {
        return toJson(data, "");
    }

    public static JavaType getCollectionType(Class<?> parametrized, Class<?> parametersFor, Class<?>... parameterClasses) {
        return mapper.getTypeFactory().constructParametrizedType(parametrized, parametersFor, parameterClasses);
    }

    public static <T> T jsonToObject(Class<T> clazz, String json) {
        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> toList(String jsonStr, Class<T> cls) {
        ObjectMapper mapper = new ObjectMapper();
        List<T> objList = null;
        try {
            JavaType t = mapper.getTypeFactory().constructParametricType(List.class, cls);
            objList = mapper.readValue(jsonStr, t);
        } catch (Exception e) {
        }
        return objList;
    }


}