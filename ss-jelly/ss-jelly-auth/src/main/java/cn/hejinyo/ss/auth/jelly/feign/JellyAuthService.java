package cn.hejinyo.ss.auth.jelly.feign;

import cn.hejinyo.ss.auth.jelly.feign.factory.JellyAuthServiceFallbackFactory;
import cn.hejinyo.ss.auth.server.service.JellyAuthApiService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/04/27 01:10
 */
@FeignClient(value = "ss-auth-server", fallbackFactory = JellyAuthServiceFallbackFactory.class)
public interface JellyAuthService extends JellyAuthApiService {
}
