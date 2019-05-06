package cn.hejinyo.ss.common.shiro.core.utils;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;

/**
 * Shiro工具类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/7/29 18:07
 */
public class ShiroUtils {
    /**
     * 生成用户密码
     */
    public static String userPassword(String userPwd, String salt) {
        String algorithmName = "md5";
        int hashIterations = 2;
        SimpleHash hash = new SimpleHash(algorithmName, userPwd, salt, hashIterations);
        return hash.toHex();
    }

    /**
     * 获取用户Subject
     */
    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    /**
     * 判断用户是否登录
     */
    public static boolean isLogin() {
        return SecurityUtils.getSubject().getPrincipal() != null;
    }

    /**
     * 用户注销
     */
    public static void logout() {
        SecurityUtils.getSubject().logout();
    }

}
