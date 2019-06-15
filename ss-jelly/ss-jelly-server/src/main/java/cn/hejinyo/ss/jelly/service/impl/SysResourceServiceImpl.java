package cn.hejinyo.ss.jelly.service.impl;

import cn.hejinyo.ss.common.consts.CommonConstant;
import cn.hejinyo.ss.common.utils.PojoConvertUtil;
import cn.hejinyo.ss.common.utils.RecursionUtil;
import cn.hejinyo.ss.jelly.dao.SysResourceDao;
import cn.hejinyo.ss.jelly.model.dto.SysResourceDTO;
import cn.hejinyo.ss.jelly.service.SysResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return RecursionUtil.listTree(Boolean.FALSE, SysResourceDTO.class, TREE_ID, list, Collections.singletonList(CommonConstant.TREE_ROOT));
    }

}
