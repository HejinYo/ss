package cn.hejinyo.ss.admin.controller;

import cn.hejinyo.ss.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/4/6 20:59
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ms/admin/")
public class UserMsController {

    private final UserService userService;

    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/getByUsername/{userName}")
    public UserDetails getByUsername(@PathVariable("userName") String username) {
        return userService.getByUsername(username);
    }
    
}
