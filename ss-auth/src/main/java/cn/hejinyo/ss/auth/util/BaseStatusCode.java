package cn.hejinyo.ss.auth.util;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/3 20:10
 */
public interface BaseStatusCode {

    /**
     * 状态码
     *
     * @return int
     */
    int getCode();

    /**
     * 状态
     *
     * @return String
     */
    String getMsg();
}
