package cn.hejinyo.ss.auth.controller;

import cn.hejinyo.ss.auth.service.SsAuthLoginService;
import cn.hejinyo.ss.auth.vo.SsAuthLoginReqVo;
import cn.hejinyo.ss.auth.vo.SsAuthLoginTokenVo;
import cn.hejinyo.ss.common.core.util.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录接口
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/3/11
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/")
public class SsAuthApiController {

    private final SsAuthLoginService ssAuthLoginService;

    /**
     * 用户登陆返回token
     */
    @PostMapping("/login")
    public Result<SsAuthLoginTokenVo> login(@RequestBody SsAuthLoginReqVo ssAuthLoginReqVo) {
        return Result.ok(ssAuthLoginService.login(ssAuthLoginReqVo));
    }

}
