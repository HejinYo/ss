package cn.hejinyo.ss.auth.jelly.token;

import lombok.Data;

import java.util.Set;

/**
 * 用户信息
 *
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/04/24 14:20
 */
@Data
public class SsUserDetails {
    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户JWT
     */
    private String jwt;

    /**
     * 用户角色
     */
    private Set<String> roleSet;

    /**
     * 用户权限
     */
    private Set<String> permSet;

    public SsUserDetails() {

    }

    public SsUserDetails(String userName, Integer userId, String jwt, Set<String> roleSet, Set<String> permSet) {
        this.userName = userName;
        this.userId = userId;
        this.jwt = jwt;
        this.roleSet = roleSet;
        this.permSet = permSet;
    }
}
