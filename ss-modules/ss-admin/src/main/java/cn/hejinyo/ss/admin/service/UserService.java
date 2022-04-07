package cn.hejinyo.ss.admin.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用户管理服务
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/4/6 21:49
 */
public interface UserService {

    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserDetails getByUsername(String username);
}
