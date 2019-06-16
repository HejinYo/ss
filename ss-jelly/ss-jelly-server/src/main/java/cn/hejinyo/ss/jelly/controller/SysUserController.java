package cn.hejinyo.ss.jelly.controller;

import cn.hejinyo.ss.jelly.service.SysUserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统用户管理
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/19 23:08
 */
@RequestMapping("/user")
@RestController
@Api(tags = "用户管理服务")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;


}

