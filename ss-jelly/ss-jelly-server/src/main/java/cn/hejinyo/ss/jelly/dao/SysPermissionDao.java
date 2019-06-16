package cn.hejinyo.ss.jelly.dao;

import cn.hejinyo.ss.common.framework.base.BaseDao;
import cn.hejinyo.ss.jelly.model.entity.SysPermissionEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

/**
 * sys_permission 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2019/06/16 20:44
 */
@Mapper
public interface SysPermissionDao extends BaseDao<SysPermissionEntity, Integer> {

    /**
     * 获取所有权限字符串列表
     */
    Set<String> findAllCode();

    /**
     * 根据用户ID获取权限字符串列表
     */
    Set<String> findCodeSetByUserId(Integer userId);
}
