package com.imamba.boot.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.serializer.JSONLibDataFormatSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.Predicate;
import com.imamba.boot.common.exception.MException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class FastJsonUtil {

    private static final Logger logger = LoggerFactory.getLogger(FastJsonUtil.class);
    private static final SerializeConfig config;

    static {
        config = new SerializeConfig();
        config.put(java.util.Date.class, new JSONLibDataFormatSerializer());
        config.put(java.sql.Date.class, new JSONLibDataFormatSerializer());
    }

    private static final SerializerFeature[] features = {
            SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteNullListAsEmpty,
            SerializerFeature.WriteNullNumberAsZero,
            SerializerFeature.WriteNullBooleanAsFalse,
            SerializerFeature.WriteNullStringAsEmpty
    };

    public static String writeValueAsString(Object value) {
        if (value == null) {
            return null;
        }
        return JSON.toJSONString(value, config, features);
    }

    public static <T> T readValue(String content, Class<T> valueType) {
        return JSON.parseObject(content, valueType);
    }

    public static <T> T readValue(String json, String jsonPath) {
        try {
            return JsonPath.read(json, jsonPath);
        } catch (PathNotFoundException e) {
            logger.debug(e.getMessage());
            return null;
        } catch (Exception e) {
            throw new MException(e);
        }
    }

    public static <T> T readValue(String json, String jsonPath, Predicate... filters) {
        try {
            return JsonPath.read(json, jsonPath, filters);
        } catch (PathNotFoundException e) {
            logger.debug(e.getMessage());
            return null;
        } catch (Exception e) {
            throw new MException(e);
        }
    }

    public static <T> List<T> readValueAsArray(String json, Class<T> valueType) {
        return JSON.parseArray(json, valueType);
    }

    public static <T> List<T> readValueAsArray(String json, String jsonPath, Class<T> valueType) {
        Object content = JSONPath.read(json, jsonPath);
        if (content == null) {
            logger.debug("path not found:{}", jsonPath);
            return Collections.emptyList();
        }
        return JSONArray.parseArray(content.toString(), valueType);
    }
}
