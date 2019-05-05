package cn.hejinyo.ss.auth.server.feign.fallback;

import cn.hejinyo.ss.auth.server.feign.JellySysUserService;
import cn.hejinyo.ss.jelly.dto.SysUserDTO;
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
    public SysUserDTO getUserInfo(Integer userId) {
        log.error("调用JellySysUserService.{} 参数:{},异常:{}", "getUserInfo", userId, cause);
        return null;
    }

    /**
     * 获取用户信息
     */
    @Override
    public SysUserDTO findByUserName(String userName) {
        log.error("调用JellySysUserService.{} 参数:{},异常:{}", "findByUserName", userName, cause);
        return null;
    }

    /**
     * 获得角色信息
     */
    @Override
    public Set<String> getUserRoleSet(int userId) {
        log.error("调用JellySysUserService.{} 参数:{},异常:{}", "getUserRoleSet", userId, cause);
        return null;
    }

    /**
     * 获得权限信息
     */
    @Override
    public Set<String> getUserPermSet(int userId) {
        log.error("调用JellySysUserService.{} 参数:{},异常:{}", "getUserPermSet", userId, cause);
        return null;
    }
}
