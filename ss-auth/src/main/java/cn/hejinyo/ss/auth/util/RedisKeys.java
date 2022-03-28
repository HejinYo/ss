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

    public static final String USER_TOKEN = "auth:user:login:token:";

}
