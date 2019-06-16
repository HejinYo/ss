package cn.hejinyo.ss.jelly.service.impl;

import cn.hejinyo.ss.common.consts.CommonConstant;
import cn.hejinyo.ss.jelly.dao.SysPermissionDao;
import cn.hejinyo.ss.jelly.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 权限管理
 *
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/06/16 20:46
 */
@Service
public class SysPermissionServiceImpl implements SysPermissionService {

    @Autowired
    private SysPermissionDao sysPermissionDao;

    /**
     * 根据用户ID获取权限字符串列表
     */
    @Override
    public Set<String> getCodeSetByUserId(Integer userId) {
        if(CommonConstant.SUPER_ADMIN.equals(userId)){
            return sysPermissionDao.findAllCode();
        }
        return sysPermissionDao.findCodeSetByUserId(userId);
    }
}
