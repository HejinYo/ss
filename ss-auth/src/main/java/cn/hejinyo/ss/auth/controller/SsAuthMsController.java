package cn.hejinyo.ss.auth.controller;

import cn.hejinyo.ss.auth.service.SsAuthLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 授权微服务
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/20 20:34
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ms/auth/")
public class SsAuthMsController {

    private final SsAuthLoginService ssAuthLoginService;

    /**
     * 获取 jwkSet
     */
    @GetMapping(value = "/jwkSet", produces = MediaType.APPLICATION_JSON_VALUE)
    public String jwkSet() {
        return ssAuthLoginService.jwkSet();
    }

    /**
     * 验证是否登陆并换取微服务 MsToken
     */
    @PostMapping("/checkAndGetMsToken")
    public String checkAndGetMsToken(@RequestBody String accessToken) {
        return ssAuthLoginService.checkAndGetMsToken(accessToken);
    }

}
