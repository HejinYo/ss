package cn.hejinyo.ss.admin.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/11/21 22:18
 */
@Slf4j
public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {

        //忽略 在json字符串中存在，但是在对象中不存在对应属性的情况，防止错误。
        // 例如json数据中多出字段，而对象中没有此字段。如果设置true，抛出异常，因为字段不对应；false则忽略多出的字段，默认值为null，将其他字段反序列化成功
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

    }

    /**
     * 将单个对象转换成json格式的字符串（没有格式化后的json）
     */
    public static <T> String toString(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("Parse object to String error", e);
            return null;
        }
    }

    /**
     * 将单个对象转换成json格式的字符串（格式化后的json）
     */
    public static <T> String toStringPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("Parse object to String error", e);
            return null;
        }
    }

    /**
     * 将json形式的字符串数据转换成单个对象
     */
    public static <T> T toObject(String str, Class<T> clazz) {
        if (!StringUtils.hasText(str) || clazz == null) {
            return null;
        }
        try {
            return objectMapper.readValue(str, clazz);
        } catch (IOException e) {
            log.warn("Parse object to Object error", e);
            return null;
        }
    }

}
