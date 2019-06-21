package cn.hejinyo.ss.jelly.service.impl;

import cn.hejinyo.ss.common.consts.CommonConstant;
import cn.hejinyo.ss.common.framework.utils.PageInfo;
import cn.hejinyo.ss.common.utils.PageQuery;
import cn.hejinyo.ss.common.utils.StringUtils;
import cn.hejinyo.ss.jelly.dao.SysPermissionDao;
import cn.hejinyo.ss.jelly.model.dto.SysPermissionPageQueryDTO;
import cn.hejinyo.ss.jelly.model.entity.SysPermissionEntity;
import cn.hejinyo.ss.jelly.service.SysPermissionService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (CommonConstant.SUPER_ADMIN.equals(userId)) {
            return sysPermissionDao.findAllCode();
        }
        return sysPermissionDao.findCodeSetByUserId(userId);
    }

    /**
     * 权限列表数据
     */
    @Override
    public PageInfo<SysPermissionEntity> pageList(PageQuery<SysPermissionPageQueryDTO> pageQuery) {
        if (StringUtils.isNotEmpty(pageQuery.getOrder())) {
            PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize(), pageQuery.getOrder());
        } else {
            PageHelper.startPage(pageQuery.getPageNum(), pageQuery.getPageSize());
        }
        return new PageInfo<>(sysPermissionDao.findPage(pageQuery));
    }
}
