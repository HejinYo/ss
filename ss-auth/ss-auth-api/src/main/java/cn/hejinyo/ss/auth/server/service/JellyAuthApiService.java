package cn.hejinyo.ss.auth.server.service;

import cn.hejinyo.ss.auth.server.dto.AuthCheckResult;
import cn.hejinyo.ss.common.consts.CommonConstant;
import cn.hejinyo.ss.common.utils.MicroserviceResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/29 23:48
 */
@RequestMapping(CommonConstant.MICRO_SERVER_API)
public interface JellyAuthApiService {

    /**
     * 校验用户token，成功则返回角色，权限
     */
    @GetMapping(value = "/jelly/auth/check/{userId}/{jti}")
    MicroserviceResult<AuthCheckResult> checkToken(@PathVariable("userId") Integer userId, @PathVariable("jti") String jti);

}
