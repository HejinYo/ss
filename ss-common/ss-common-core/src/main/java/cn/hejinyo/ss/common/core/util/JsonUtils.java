package cn.hejinyo.ss.common.core.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * JSON工具
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/11/21 22:18
 */
@Slf4j
@UtilityClass
public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        // 忽略多余的字段
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    }

    /**
     * 对象转json
     *
     * @param obj Object
     * @param <T> Object
     * @return String
     */
    public static <T> String toJson(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : OBJECT_MAPPER.writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("JsonUtils:对象转json失败", e);
            return null;
        }
    }

    /**
     * 对象转格式化json
     *
     * @param obj Object
     * @param <T> Object
     * @return String
     */
    public static <T> String toJsonPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            log.warn("JsonUtils:对象转json失败", e);
            return null;
        }
    }

    /**
     * 将json转对象
     *
     * @param str   String
     * @param clazz T
     * @param <T>   T
     * @return T
     */
    public static <T> T toObject(String str, Class<T> clazz) {
        if (!StringUtils.hasText(str) || clazz == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(str, clazz);
        } catch (IOException e) {
            log.warn("JsonUtils: json转对象失败", e);
            return null;
        }
    }

}
