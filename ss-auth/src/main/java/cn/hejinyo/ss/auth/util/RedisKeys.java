package cn.hejinyo.ss.auth.util;

import lombok.Data;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/3/22 20:05
 */
@Data
public class RedisKeys {

    public final static String USER_TOKEN = "auth:user:login:token";
}
