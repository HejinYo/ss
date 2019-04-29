package cn.hejinyo.ss.auth.server.feign.factory;

import cn.hejinyo.ss.auth.server.feign.JellySysUserService;
import cn.hejinyo.ss.auth.server.feign.fallback.JellySysUserServiceFallbackImpl;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 服务降级回退工厂
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/29 23:21
 */
@Component
public class JellySysUserServiceFallbackFactory implements FallbackFactory<JellySysUserService> {

    @Override
    public JellySysUserService create(Throwable throwable) {
        JellySysUserServiceFallbackImpl jellySysUserServiceFallback = new JellySysUserServiceFallbackImpl();
        jellySysUserServiceFallback.setCause(throwable);
        return jellySysUserServiceFallback;
    }
}
