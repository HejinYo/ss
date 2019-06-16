package cn.hejinyo.ss.jelly.service;

import cn.hejinyo.ss.auth.jelly.token.SsUserDetails;
import cn.hejinyo.ss.jelly.model.dto.SysResourceDTO;
import cn.hejinyo.ss.jelly.model.dto.SysUserDTO;

import java.util.HashMap;
import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/5/30 22:04
 */
public interface SysUserService {

    /**
     * 根据用户名查询用户信息
     *
     * @param userName 用户名
     */
    SysUserDTO getByUserName(String userName);

    /**
     * 获取菜单信息
     *
     * @param userDetails 登录用户信息
     */
    List<SysResourceDTO> getUserMenus(SsUserDetails userDetails);
}
