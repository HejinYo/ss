package cn.hejinyo.ss.auth.jelly.feign.factory;

import cn.hejinyo.ss.auth.jelly.feign.JellyAuthService;
import cn.hejinyo.ss.auth.jelly.feign.fallback.JellyAuthServiceFallbackImpl;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 服务降级回退工厂
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/29 23:21
 */
@Component
public class JellyAuthServiceFallbackFactory implements FallbackFactory<JellyAuthService> {

    @Override
    public JellyAuthService create(Throwable throwable) {
        JellyAuthServiceFallbackImpl jellySysUserServiceFallback = new JellyAuthServiceFallbackImpl();
        jellySysUserServiceFallback.setCause(throwable);
        return jellySysUserServiceFallback;
    }
}
