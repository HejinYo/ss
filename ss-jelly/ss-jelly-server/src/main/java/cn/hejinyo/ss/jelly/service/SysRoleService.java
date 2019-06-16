package cn.hejinyo.ss.jelly.service;

import java.util.Set;

/**
 * 角色管理
 *
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/06/16 21:06
 */
public interface SysRoleService {

    /**
     * 根据用户ID获取角色字符串列表
     */
    Set<String> getRoleCodeSetByUserId(int userId);
}
