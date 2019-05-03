package cn.hejinyo.ss.auth.server.feign;

import cn.hejinyo.ss.auth.server.feign.factory.JellySysUserServiceFallbackFactory;
import cn.hejinyo.ss.jelly.service.SysUserApiService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/04/27 01:10
 */
@FeignClient(value = "ss-jelly-server", fallbackFactory = JellySysUserServiceFallbackFactory.class)
public interface JellySysUserService extends SysUserApiService {
}
