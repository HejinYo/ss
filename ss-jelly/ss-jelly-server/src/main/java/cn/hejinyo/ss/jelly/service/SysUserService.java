package cn.hejinyo.ss.jelly.service;

import cn.hejinyo.ss.auth.jelly.token.SsUserDetails;
import cn.hejinyo.ss.jelly.model.dto.SysResourceDTO;
import cn.hejinyo.ss.jelly.model.dto.SysUserDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
     * @param userId  用户编号
     * @param roleSet 用户角色
     */
    List<SysResourceDTO> getUserMenus(Integer userId, Set<String> roleSet);
}
