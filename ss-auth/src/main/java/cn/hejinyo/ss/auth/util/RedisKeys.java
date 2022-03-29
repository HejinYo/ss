package cn.hejinyo.ss.auth.util;

import lombok.Data;
import lombok.experimental.UtilityClass;

/**
 * redis keys
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/3/22 20:05
 */
@Data
@UtilityClass
public class RedisKeys {

    /**
     * 用户token存放
     *
     * @param id String
     * @return String
     */
    public static String userToken(String id) {
        return "auth:user:login:token:" + id;
    }
}
