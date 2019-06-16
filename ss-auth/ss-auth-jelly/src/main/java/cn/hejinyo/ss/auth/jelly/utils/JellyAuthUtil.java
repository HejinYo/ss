package cn.hejinyo.ss.auth.jelly.utils;

import cn.hejinyo.ss.auth.jelly.token.SsUserDetails;
import org.apache.shiro.SecurityUtils;

import java.util.Optional;

/**
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/06/16 19:55
 */
public class JellyAuthUtil {

    /**
     * 获取用户信息
     */
    public static SsUserDetails getUserInfo() {
        return Optional.ofNullable(SecurityUtils.getSubject())
                .map(v -> (SsUserDetails) v.getPrincipal())
                .orElse(null);
    }
}
