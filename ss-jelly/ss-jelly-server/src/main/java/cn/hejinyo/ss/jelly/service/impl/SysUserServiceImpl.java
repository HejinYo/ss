package cn.hejinyo.ss.jelly.service.impl;

import cn.hejinyo.ss.auth.jelly.token.SsUserDetails;
import cn.hejinyo.ss.common.consts.CommonConstant;
import cn.hejinyo.ss.common.utils.PojoConvertUtil;
import cn.hejinyo.ss.jelly.dao.SysUserDao;
import cn.hejinyo.ss.jelly.model.dto.SysResourceDTO;
import cn.hejinyo.ss.jelly.model.dto.SysUserDTO;
import cn.hejinyo.ss.jelly.model.entity.SysUserEntity;
import cn.hejinyo.ss.jelly.service.SysResourceService;
import cn.hejinyo.ss.jelly.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/5/30 22:04
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private SysResourceService sysResourceService;

    /**
     * 根据用户名查询用户信息
     *
     * @param userName 用户名
     */
    @Override
    public SysUserDTO getByUserName(String userName) {
        SysUserEntity userEntity = sysUserDao.findByUserName(userName);
        return Optional.ofNullable(userEntity).map(v -> PojoConvertUtil.convert(v, SysUserDTO.class)).orElse(null);
    }

    /**
     * 获取菜单信息
     *
     * @param userId  用户编号
     * @param roleSet 用户角色
     */
    @Override
    public List<SysResourceDTO> getUserMenus(Integer userId, Set<String> roleSet) {
        // 超级管理员查询所有
        if (CommonConstant.SUPER_ADMIN.equals(userId)) {
            // 直接返回所有菜单资源
            return sysResourceService.getAllMenus();
        }
        // 普通用户根据角色查询
        return sysResourceService.getMenusByRoleSet(roleSet);
    }
}
