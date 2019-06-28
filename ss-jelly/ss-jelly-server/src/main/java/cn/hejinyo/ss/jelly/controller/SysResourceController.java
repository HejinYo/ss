package cn.hejinyo.ss.jelly.controller;

import cn.hejinyo.ss.auth.jelly.utils.JellyAuthUtil;
import cn.hejinyo.ss.common.framework.consts.StatusCode;
import cn.hejinyo.ss.common.framework.utils.Result;
import cn.hejinyo.ss.common.model.validator.RestfulValid;
import cn.hejinyo.ss.common.shiro.core.utils.ShiroUtils;
import cn.hejinyo.ss.jelly.model.entity.SysResourceEntity;
import cn.hejinyo.ss.jelly.service.SysResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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


    /**
     * 增加
     */
    @ApiOperation(value = "增加一个资源", notes = "增加一个资源")
    @PostMapping
    public Result save(@Validated(RestfulValid.POST.class) @RequestBody SysResourceEntity sysResource) {
        int result = sysResourceService.saveResource(sysResource, JellyAuthUtil.getUserInfo());
        if (result > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_SAVE_FAILURE);
    }

    /**
     * 更新
     */
    @ApiOperation(value = "更新资源信息", notes = "更新资源详细信息")
    @PutMapping(value = "/{resId}")
    public Result update(@PathVariable("resId") Integer resId, @Validated(RestfulValid.PUT.class) @RequestBody SysResourceEntity sysResource) {
        int result = sysResourceService.updateResource(resId, sysResource);
        if (result > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_UPDATE_FAILURE);
    }

    /**
     * 删除
     */
    @ApiOperation(value = "删除资源", notes = "删除资源：/delete/1")
    @DeleteMapping(value = "/{resId}")
    public Result delete(@PathVariable("resId") Integer resId) {
        int result = sysResourceService.deleteResource(resId);
        if (result > 0) {
            return Result.ok();
        }
        return Result.error(StatusCode.DATABASE_DELETE_FAILURE);
    }


    /**
     * 修改排序
     */


}
