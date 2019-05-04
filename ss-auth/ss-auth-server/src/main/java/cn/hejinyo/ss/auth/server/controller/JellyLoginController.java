package cn.hejinyo.ss.auth.server.controller;

import cn.hejinyo.ss.auth.server.service.JellyService;
import cn.hejinyo.ss.common.framework.utils.JwtTools;
import cn.hejinyo.ss.common.framework.utils.Result;
import cn.hejinyo.ss.common.model.validator.RestfulValid;
import cn.hejinyo.ss.common.utils.StringUtils;
import cn.hejinyo.ss.jelly.dto.UserNameLoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * jelly登录控制器
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/9/1 22:47
 */
@RestController
@RequestMapping("/jelly")
public class JellyLoginController {

    @Autowired
    private JellyService jellyService;


    /**
     * 执行登录,返回userToken
     */
    @PostMapping(value = "/login")
    public Result login(@Validated(RestfulValid.POST.class) @RequestBody UserNameLoginDTO userNameLoginVO) {
        return Result.result(jellyService.doLogin(jellyService.checkUser(userNameLoginVO)));
    }

    /**
     * 登出
     */
    @PutMapping(value = "/logout")
    public Result logout(HttpServletRequest request) {
        String userToken = request.getHeader(JwtTools.AUTHOR_PARAM);
        if (StringUtils.isNotEmpty(userToken)) {
            jellyService.logout(userToken);
        }
        return Result.ok();
    }

}
