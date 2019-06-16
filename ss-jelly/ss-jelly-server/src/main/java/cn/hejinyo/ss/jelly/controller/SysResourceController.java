package cn.hejinyo.ss.jelly.controller;

import cn.hejinyo.ss.common.framework.utils.Result;
import cn.hejinyo.ss.jelly.service.SysResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/06/15 17:21
 */
@RestController
@RequestMapping("/resource")
@Api(tags = "资源管理服务")
public class SysResourceController {


    @Autowired
    private SysResourceService sysResourceService;

    /**
     * 资源管理树数据
     */
    @ApiOperation(value = "资源管理树数据", notes = "资源管理树数据")
    @GetMapping("/operateTree")
    // @RequiresPermissions("sys:resource:view")
    public Result editTree() {
        return Result.result(sysResourceService.getOperateTree());
    }


}
