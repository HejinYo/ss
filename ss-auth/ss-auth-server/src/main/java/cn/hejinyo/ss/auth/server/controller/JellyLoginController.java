package cn.hejinyo.ss.auth.server.controller;

import cn.hejinyo.ss.auth.server.service.JellyService;
import cn.hejinyo.ss.common.consts.CommonConstant;
import cn.hejinyo.ss.common.framework.utils.JwtTools;
import cn.hejinyo.ss.common.framework.utils.Result;
import cn.hejinyo.ss.common.model.validator.RestfulValid;
import cn.hejinyo.ss.common.utils.StringUtils;
import cn.hejinyo.ss.jelly.model.dto.UserNameLoginDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jelly登录控制器
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/9/1 22:47
 */
@RestController
@RequestMapping("/" + CommonConstant.JELLY_AUTH)
@Api(tags = "jelly登录服务")
public class JellyLoginController {

    @Autowired
    private JellyService jellyService;

    /**
     * 执行登录,返回userToken
     */
    @PostMapping(value = "/login")
    @ApiOperation(value = "执行登录", notes = "返回userToken")
    @ApiImplicitParam(paramType = "body", name = "userNameLoginVO", value = "用户信息对象", required = true, dataType = "UserNameLoginDTO")
    public Result login(@Validated(RestfulValid.POST.class) @RequestBody UserNameLoginDTO userNameLoginVO, HttpServletResponse response) {
        String token = jellyService.doLogin(jellyService.checkUser(userNameLoginVO));
        // 放入cookie
        Cookie cookie = new Cookie(JwtTools.AUTHOR_PARAM, token);
        cookie.setPath("/");
        response.addCookie(cookie);
        return Result.ok(token);
    }

    /**
     * 登出
     */
    @PutMapping(value = "/logout")
    @ApiOperation(value = "登出", notes = "返回userToken")
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
        String userToken = null;
        // 先从cookie中获取
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(JwtTools.AUTHOR_PARAM)) {
                    userToken = cookie.getValue();
                    break;
                }
            }
        }
        // cookie中没有，从header中获取
        if (StringUtils.isEmpty(userToken)) {
            userToken = request.getHeader(JwtTools.AUTHOR_PARAM);
        }
        // header没有，从param中获取
        if (StringUtils.isEmpty(userToken)) {
            userToken = request.getParameter(JwtTools.AUTHOR_PARAM);
        }
        if (StringUtils.isNotEmpty(userToken)) {
            jellyService.logout(userToken);
            Cookie cookie = new Cookie(JwtTools.AUTHOR_PARAM, "");
            cookie.setPath("/");
            // 设置保存cookie最大时长为0，即使其失效
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        return Result.result(userToken);
    }

}
