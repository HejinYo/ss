package cn.hejinyo.ss.auth.jelly.feign.fallback;

import cn.hejinyo.ss.auth.jelly.feign.JellyAuthService;
import cn.hejinyo.ss.auth.server.dto.AuthCheckResult;
import cn.hejinyo.ss.common.utils.MicroserviceResult;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务降级实现
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/29 23:17
 */
@Slf4j
public class JellyAuthServiceFallbackImpl implements JellyAuthService {

    @Setter
    private Throwable cause;

    /**
     * 校验用户token，成功则返回角色，权限
     */
    @Override
    public MicroserviceResult<AuthCheckResult> checkToken(Integer userId, String jti) {
        log.error("JellyAuthService.{} 参数:userId:{},jti:{},异常:{}", "checkToken", userId, jti, cause);
        return MicroserviceResult.fallback("JellyAuthService.{} 参数:userId:{},jti:{},异常:{}", "findByUserName", userId, jti);
    }
}
