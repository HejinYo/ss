package cn.hejinyo.ss.jelly.api;

import cn.hejinyo.ss.auth.jelly.token.SsUserDetails;
import cn.hejinyo.ss.auth.jelly.utils.JellyAuthUtil;
import cn.hejinyo.ss.common.framework.utils.Result;
import cn.hejinyo.ss.common.utils.MicroserviceResult;
import cn.hejinyo.ss.common.utils.PojoConvertUtil;
import cn.hejinyo.ss.jelly.model.dto.SysUserDTO;
import cn.hejinyo.ss.jelly.service.SysPermissionService;
import cn.hejinyo.ss.jelly.service.SysRoleService;
import cn.hejinyo.ss.jelly.service.SysUserApiService;
import cn.hejinyo.ss.jelly.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 微服务api接口
 *
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/04/27 00:58
 */
@RestController
@Api(tags = "jelly微服务api接口")
@Slf4j
public class SysUserApi implements SysUserApiService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysPermissionService sysPermissionService;

    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 根据用户名查询用户信息
     */
    @Override
    @ApiOperation(value = "根据用户名查询用户信息", notes = "findByUserName")
    public MicroserviceResult<SysUserDTO> getByUserName(@PathVariable String userName) {
        return MicroserviceResult.ok(sysUserService.getByUserName(userName));
    }

    /**
     * 获得角色信息
     */
    @Override
    @ApiOperation(value = "获得角色信息", notes = "返回角色字符串Set<String>")
    public MicroserviceResult<Set<String>> getUserRoleSet(@PathVariable int userId) {
        return MicroserviceResult.ok(sysRoleService.getRoleCodeSetByUserId(userId));
    }

    /**
     * 获得权限信息
     */
    @Override
    @ApiOperation(value = "获得权限信息", notes = "返回权限字符串Set<String>")
    public MicroserviceResult<Set<String>> getUserPermSet(@PathVariable int userId) {
        return MicroserviceResult.ok(sysPermissionService.getCodeSetByUserId(userId));
    }

    /**
     * 获取用户菜单
     */
    @Override
    @ApiOperation(value = "获取用户菜单", notes = "获取用户菜单")
    public MicroserviceResult<List> getUserMenus(Integer userId, Set<String> roleSet) {
        return MicroserviceResult.ok(sysUserService.getUserMenus(userId, roleSet));
    }
}
