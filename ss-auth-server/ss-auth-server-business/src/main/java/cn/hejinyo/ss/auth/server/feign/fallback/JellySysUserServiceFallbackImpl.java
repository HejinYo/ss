package cn.hejinyo.ss.auth.server.feign.fallback;

import cn.hejinyo.ss.auth.server.feign.JellySysUserService;
import cn.hejinyo.ss.jelly.dto.SysUserDTO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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
}
