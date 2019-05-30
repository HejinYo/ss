package cn.hejinyo.ss.jelly.api;

import cn.hejinyo.ss.common.utils.MicroserviceResult;
import cn.hejinyo.ss.common.utils.PojoConvertUtil;
import cn.hejinyo.ss.jelly.model.dto.SysUserDTO;
import cn.hejinyo.ss.jelly.service.SysUserApiService;
import cn.hejinyo.ss.jelly.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    /**
     * 根据用户名查询用户信息
     */
    @Override
    @ApiOperation(value = "根据用户名查询用户信息", notes = "findByUserName")
    public MicroserviceResult<SysUserDTO> findByUserName(@PathVariable String userName) {
        return MicroserviceResult.ok(sysUserService.getByUserName(userName));
    }

    /**
     * 获得角色信息
     */
    @Override
    @ApiOperation(value = "获得角色信息", notes = "返回角色字符串Set<String>")
    public MicroserviceResult<Set<String>> getUserRoleSet(@PathVariable int userId) {
        return MicroserviceResult.ok(new HashSet<>(Arrays.asList("admin", "class")));
    }

    /**
     * 获得权限信息
     */
    @Override
    @ApiOperation(value = "获得权限信息", notes = "返回权限字符串Set<String>")
    public MicroserviceResult<Set<String>> getUserPermSet(@PathVariable int userId) {
        return MicroserviceResult.ok(new HashSet<>(Arrays.asList("select", "update")));
    }
}
