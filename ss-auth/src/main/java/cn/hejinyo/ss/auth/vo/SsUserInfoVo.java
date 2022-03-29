package cn.hejinyo.ss.auth.vo;

import lombok.Data;

import java.util.Set;

/**
 * 用户信息
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/3/29 20:45
 */
@Data
public class SsUserInfoVo {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户权限字符串
     */
    private Set<String> authorities;
}
