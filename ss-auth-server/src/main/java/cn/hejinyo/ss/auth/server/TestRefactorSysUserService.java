package cn.hejinyo.ss.auth.server;

import cn.hejinyo.ss.jelly.api.SysUserApiService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/04/27 01:10
 */
@FeignClient(value = "ss-jelly-server")
public interface TestRefactorSysUserService extends SysUserApiService {
}
