package cn.hejinyo.ss.jelly.dao;

import cn.hejinyo.ss.common.framework.base.BaseDao;
import cn.hejinyo.ss.jelly.model.dto.SysResourceDTO;
import cn.hejinyo.ss.jelly.model.entity.SysResourceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * sys_resource 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2019/06/15 17:16
 */
@Mapper
public interface SysResourceDao extends BaseDao<SysResourceEntity, Integer> {

    /**
     * 查询所有资源列表
     */
    List<SysResourceDTO> findAllResourceList();

    /**
     * 根据角色查询
     */
    List<SysResourceDTO> findResourceListByRoleSet(@Param("roleSet") Set<String> roleSet);

    /**
     * 获取系统所有有效资源列表，状态正常
     */
    List<SysResourceDTO> findValidResourceList();
}
