package cn.hejinyo.ss.auth.server.service;


import cn.hejinyo.ss.auth.server.dto.AuthCheckResult;
import cn.hejinyo.ss.jelly.model.dto.SysUserDTO;
import cn.hejinyo.ss.jelly.model.dto.UserNameLoginDTO;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/9/2 18:54
 */
public interface JellyService {

    /**
     * 验证登录用户
     */
    SysUserDTO checkUser(UserNameLoginDTO userNameLoginVO);

    /**
     * 处理登录逻辑
     */
    String doLogin(SysUserDTO userDTO);

    /**
     * 验证token 通过返回角色权限信息
     */
    AuthCheckResult checkToken(Integer userId, String jti);

    /**
     * 注销
     */
    void logout(String token);
}
