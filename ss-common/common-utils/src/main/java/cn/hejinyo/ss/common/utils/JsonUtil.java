package cn.hejinyo.ss.common.utils;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Type;
import java.util.Map;

public final class JsonUtil {

    /**
     * 序列化
     */
    public static String toJSONString(Object entity) {
        return JSON.toJSONString(entity);
    }

    /**
     * 反序列化
     */
    public static <T> T parseObject(String json, Type type) {
        return JSON.parseObject(json, type);
    }

    /**
     * 反序列化
     */
    public static <T> T parseObject(String json, Class<T> c) {
        return JSON.parseObject(json, c);
    }

    /**
     * 转map
     */
    public static Map toMap(String json) {
        return JSON.parseObject(json, Map.class);
    }


}