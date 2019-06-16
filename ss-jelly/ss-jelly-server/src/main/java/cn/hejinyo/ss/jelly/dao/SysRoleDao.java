package cn.hejinyo.ss.jelly.dao;

import cn.hejinyo.ss.common.framework.base.BaseDao;
import cn.hejinyo.ss.jelly.model.entity.SysRoleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

/**
 * sys_role 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2019/06/16 21:03
 */
@Mapper
public interface SysRoleDao extends BaseDao<SysRoleEntity, Integer> {

    /**
     * 根据用户ID获取角色字符串列表
     */
    Set<String> findRoleCodeSetByUserId(int userId);
}
