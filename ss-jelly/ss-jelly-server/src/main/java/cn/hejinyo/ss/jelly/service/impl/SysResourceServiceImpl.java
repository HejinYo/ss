package cn.hejinyo.ss.jelly.service.impl;

import cn.hejinyo.ss.auth.jelly.token.SsUserDetails;
import cn.hejinyo.ss.common.consts.CommonConstant;
import cn.hejinyo.ss.common.utils.RecursionUtil;
import cn.hejinyo.ss.jelly.dao.SysResourceDao;
import cn.hejinyo.ss.jelly.model.dto.SysResourceDTO;
import cn.hejinyo.ss.jelly.model.entity.SysResourceEntity;
import cn.hejinyo.ss.jelly.service.SysResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 系统资源管理
 *
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/06/15 17:22
 */
@Service
public class SysResourceServiceImpl implements SysResourceService {

    private static final String TREE_ID = "getResId";

    @Autowired
    private SysResourceDao sysResourceDao;

    /**
     * 资源管理树数据
     */
    @Override
    public HashMap<String, List<SysResourceDTO>> getOperateTree() {
        List<SysResourceDTO> list = sysResourceDao.findAllResourceList();
        return RecursionUtil.listTree(Boolean.TRUE, SysResourceDTO.class, TREE_ID,
                list, Collections.singletonList(CommonConstant.TREE_ROOT));
    }

    /**
     * 所有有效菜单资源
     */
    @Override
    public List<SysResourceDTO> getAllMenus() {
        List<SysResourceDTO> list = sysResourceDao.findValidResourceList();
        return RecursionUtil.tree(Boolean.FALSE, SysResourceDTO.class, TREE_ID,
                list, Collections.singletonList(CommonConstant.TREE_ROOT));
    }

    /**
     * 根据角色查询有效用户菜单
     */
    @Override
    public List<SysResourceDTO> getMenusByRoleSet(Set<String> roleSet) {
        if (roleSet != null && roleSet.size() > 0) {
            List<SysResourceDTO> list = sysResourceDao.findResourceListByRoleSet(roleSet);
            return RecursionUtil.tree(Boolean.FALSE, SysResourceDTO.class, TREE_ID,
                    list, Collections.singletonList(CommonConstant.TREE_ROOT));
        }
        return new ArrayList<>();
    }

    /**
     * 增加一个资源
     */
    @Override
    public int saveResource(SysResourceEntity sysResource, SsUserDetails ssUserDetails) {
        sysResource.setCreateId(ssUserDetails.getUserId());
        sysResource.setCreateTime(new Date());
        return sysResourceDao.save(sysResource);
    }

    /**
     * 更新资源信息
     */
    @Override
    public int updateResource(Integer resId, SysResourceEntity sysResource) {
        return sysResourceDao.update(sysResource);
    }

    /**
     * 删除资源
     */
    @Override
    public int deleteResource(Integer resId) {
        return sysResourceDao.deleteByPk(resId);
    }

}
