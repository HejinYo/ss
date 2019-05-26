package cn.hejinyo.ss.jelly.controller;

import cn.hejinyo.ss.auth.jelly.feign.JellyAuthService;
import cn.hejinyo.ss.auth.server.dto.AuthCheckResult;
import cn.hejinyo.ss.common.framework.exception.InfoException;
import cn.hejinyo.ss.jelly.dao.SysUserDao;
import cn.hejinyo.ss.jelly.entity.SysUserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.*;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/19 23:08
 */
@RequestMapping("/")
@RestController
@Api(tags = "用户管理服务")
public class SysUserController {
    @Autowired
    private SysUserDao sysUserDao;


    @Autowired
    private JellyAuthService jellyAuthService;

    @GetMapping("/user/{id}")
    @ApiOperation(value = "获取用户信息", notes = "getById")
    public SysUserEntity getById(@PathVariable("id") Integer id) {
        return sysUserDao.getOne(id);
    }


    @GetMapping("/auth/{id}")
    @ApiOperation(value = "验证token", notes = "auth")
    public AuthCheckResult auth(@PathVariable("id") Integer id, @RequestParam("jti") String jti) {
        return jellyAuthService.checkToken(id, jti).get();
    }


}

