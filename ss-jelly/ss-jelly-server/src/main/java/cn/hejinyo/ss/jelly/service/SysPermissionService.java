package cn.hejinyo.ss.jelly.service;

import java.util.List;
import java.util.Set;

/**
 * 权限管理
 *
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/06/16 20:46
 */
public interface SysPermissionService {

    /**
     *
     * 根据用户ID获取权限字符串列表
     */
    Set<String> getCodeSetByUserId(Integer userId);
}
