package cn.hejinyo.ss.jelly.controller;

import cn.hejinyo.ss.auth.jelly.feign.JellyAuthService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/19 23:08
 */
@RequestMapping("/")
@RestController
@Api(tags = "用户管理服务")
public class SysUserController {
    @Autowired
    private JellyAuthService jellyAuthService;


}

