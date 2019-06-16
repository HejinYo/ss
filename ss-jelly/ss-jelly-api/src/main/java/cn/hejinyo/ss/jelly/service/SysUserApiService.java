package cn.hejinyo.ss.jelly.service;

import cn.hejinyo.ss.common.consts.CommonConstant;
import cn.hejinyo.ss.common.utils.MicroserviceResult;
import cn.hejinyo.ss.jelly.model.dto.SysUserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

/**
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/04/27 00:57
 */

@RequestMapping(CommonConstant.MICRO_SERVER_API)
public interface SysUserApiService {

    /**
     * 获取用户信息
     */
    @GetMapping("/info/findByUserName/{userName}")
    MicroserviceResult<SysUserDTO> getByUserName(@PathVariable String userName);

    /**
     * 获得角色信息
     */
    @GetMapping("/roleSet/{userId}")
    MicroserviceResult<Set<String>> getUserRoleSet(@PathVariable int userId);

    /**
     * 获得权限信息
     */
    @GetMapping("/permSet/{userId}")
    MicroserviceResult<Set<String>> getUserPermSet(@PathVariable int userId);
}
