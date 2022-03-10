package cn.hejinyo.ss.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/20 20:34
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ms/auth/")
public class AuthFeignController {

    /**
     * 通过访问token获取业务token
     *
     * @param accessToken 访问token
     * @return String
     */
    @PostMapping("/getMsToken")
    public String getMsToken(@RequestBody String accessToken) {
        return "msToken";
    }

}
