package cn.hejinyo.ss.auth.server.service;

import cn.hejinyo.ss.auth.server.dto.AuthCheckResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/29 23:48
 */
@RequestMapping("/auth")
public interface JellyAuthApiService {
    /**
     * 校验用户token，成功则返回角色，权限
     */
    @GetMapping(value = "/check/{userId}/{jti}")
    AuthCheckResult checkToken(@PathVariable("userId") Integer userId, @PathVariable("jti") String jti);

}
