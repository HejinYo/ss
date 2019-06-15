package cn.hejinyo.ss.jelly.service;

import cn.hejinyo.ss.jelly.model.dto.SysResourceDTO;
import cn.hejinyo.ss.jelly.model.entity.SysResourceEntity;

import java.util.HashMap;
import java.util.List;

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
}
