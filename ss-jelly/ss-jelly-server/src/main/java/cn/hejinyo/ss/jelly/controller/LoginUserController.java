package cn.hejinyo.ss.jelly.controller;

import cn.hejinyo.ss.auth.jelly.token.SsUserDetails;
import cn.hejinyo.ss.auth.jelly.utils.JellyAuthUtil;
import cn.hejinyo.ss.common.framework.utils.Result;
import cn.hejinyo.ss.jelly.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/06/16 20:34
 */
@RequestMapping("/login")
@RestController
@Api(tags = "登录用户服务")
public class LoginUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 获取用户菜单
     */
    @ApiOperation(value = "获取用户菜单", notes = "获取用户菜单")
    @GetMapping("/userMenus")
    public Result getUserMenus() {
        SsUserDetails userDetails = JellyAuthUtil.getUserInfo();
        return Result.result(sysUserService.getUserMenus(userDetails));
    }
}
