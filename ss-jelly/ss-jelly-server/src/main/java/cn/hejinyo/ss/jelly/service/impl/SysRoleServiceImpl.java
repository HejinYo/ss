package cn.hejinyo.ss.jelly.service.impl;

import cn.hejinyo.ss.jelly.dao.SysRoleDao;
import cn.hejinyo.ss.jelly.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 角色管理
 *
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/06/16 21:06
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleDao sysRoleDao;

    /**
     * 根据用户ID获取角色字符串列表
     */
    @Override
    public Set<String> getRoleCodeSetByUserId(int userId) {
        return sysRoleDao.findRoleCodeSetByUserId(userId);
    }
}
