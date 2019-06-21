package cn.hejinyo.ss.jelly.controller;

import cn.hejinyo.ss.common.framework.utils.Result;
import cn.hejinyo.ss.common.utils.PageQuery;
import cn.hejinyo.ss.jelly.model.dto.SysPermissionPageQueryDTO;
import cn.hejinyo.ss.jelly.service.SysPermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/06/15 17:21
 */
@RestController
@RequestMapping("/permission")
@Api(tags = "权限管理服务")
public class SysPermissionController {


    @Autowired
    private SysPermissionService sysPermissionService;

    /**
     * 权限列表
     */
    @ApiOperation(value = "权限列表数据", notes = "权限列表数据")
    @PostMapping("/pageList")
    public Result pageList(@RequestBody PageQuery<SysPermissionPageQueryDTO> pageQuery) {
        return Result.result(sysPermissionService.pageList(pageQuery));
    }


}
