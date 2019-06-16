package cn.hejinyo.ss.auth.server.feign.fallback;

import cn.hejinyo.ss.auth.server.feign.JellySysUserService;
import cn.hejinyo.ss.common.utils.MicroserviceResult;
import cn.hejinyo.ss.jelly.model.dto.SysUserDTO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

/**
 * 服务降级实现
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/29 23:17
 */
@Slf4j
public class JellySysUserServiceFallbackImpl implements JellySysUserService {

    @Setter
    private Throwable cause;

    /**
     * 获取用户信息
     */
    @Override
    public MicroserviceResult<SysUserDTO> getByUserName(String userName) {
        log.error("调用JellySysUserService.{} 参数:{},异常:{}", "findByUserName", userName, cause);
        return MicroserviceResult.fallback("调用JellySysUserService.{} 参数:{},异常:{}",
                "findByUserName", userName, cause.getMessage());
    }

    /**
     * 获得角色信息
     */
    @Override
    public MicroserviceResult<Set<String>> getUserRoleSet(int userId) {
        log.error("调用JellySysUserService.{} 参数:{},异常:{}", "getUserRoleSet", userId, cause);
        return MicroserviceResult.fallback("调用JellySysUserService.{} 参数:{}", "getUserRoleSet", userId);
    }

    /**
     * 获得权限信息
     */
    @Override
    public MicroserviceResult<Set<String>> getUserPermSet(int userId) {
        log.error("调用JellySysUserService.{} 参数:{},异常:{}", "getUserPermSet", userId, cause);
        return MicroserviceResult.fallback("调用JellySysUserService.{} 参数:{}", "getUserPermSet", userId);
    }
}
