package cn.hejinyo.ss.jelly.service;

import cn.hejinyo.ss.auth.jelly.token.SsUserDetails;
import cn.hejinyo.ss.jelly.model.dto.SysResourceDTO;
import cn.hejinyo.ss.jelly.model.entity.SysResourceEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 系统资源管理
 *
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/06/15 17:22
 */
public interface SysResourceService {

    /**
     * 资源管理树数据
     */
    HashMap<String, List<SysResourceDTO>> getOperateTree();

    /**
     * 所有有效菜单资源
     */
    List<SysResourceDTO> getAllMenus();

    /**
     * 根据角色查询有效用户菜单
     */
    List<SysResourceDTO> getMenusByRoleSet(Set<String> roleSet);

    /**
     * 增加一个资源
     */
    int saveResource(SysResourceEntity sysResource, SsUserDetails ssUserDetails);

    /**
     * 更新资源信息
     */
    int updateResource(Integer resId, SysResourceEntity sysResource);

    /**
     * 删除资源
     */
    int deleteResource(Integer resId);
}
