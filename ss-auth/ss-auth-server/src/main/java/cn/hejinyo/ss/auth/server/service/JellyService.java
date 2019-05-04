package cn.hejinyo.ss.auth.server.service;


import cn.hejinyo.ss.auth.server.dto.AuthCheckResult;
import cn.hejinyo.ss.jelly.dto.PhoneLoginDTO;
import cn.hejinyo.ss.jelly.dto.SysUserDTO;
import cn.hejinyo.ss.jelly.dto.UserNameLoginDTO;

import java.util.Set;

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
     * 查找用户编号对应的角色编码字符串
     */
    Set<String> getUserRoleSet(int userId);

    /**
     * 查找用户编号对应的权限编码字符串
     */
    Set<String> getUserPermSet(int userId);

    /**
     * 验证token 通过返回角色权限信息
     */
    AuthCheckResult checkToken(Integer userId, String jti);

    /**
     * 注销
     */
    void logout(String token);

    /**
     * 发送电话登录验证码
     */
    boolean sendPhoneCode(String phone);

    /**
     * 手机用户登录
     */
    SysUserDTO phoneLogin(PhoneLoginDTO phoneLogin);
}
