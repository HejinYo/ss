package cn.hejinyo.ss.jelly.service;

import cn.hejinyo.ss.common.consts.CommonConstant;
import cn.hejinyo.ss.jelly.dto.SysUserDTO;
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
    @GetMapping("/info/{userId}")
    SysUserDTO getUserInfo(@PathVariable Integer userId);

    /**
     * 获取用户信息
     */
    @GetMapping("/info/findByUserName/{userName}")
    SysUserDTO findByUserName(@PathVariable String userName);

    /**
     * 获得角色信息
     */
    @GetMapping("/roleSet/{userId}")
    Set<String> getUserRoleSet(@PathVariable int userId);

    /**
     * 获得权限信息
     */
    @GetMapping("/permSet/{userId}")
    Set<String> getUserPermSet(@PathVariable int userId);
}