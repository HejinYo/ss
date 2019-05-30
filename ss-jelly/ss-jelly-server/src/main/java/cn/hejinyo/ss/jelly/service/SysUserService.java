package cn.hejinyo.ss.jelly.service;

import cn.hejinyo.ss.jelly.model.dto.SysUserDTO;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/5/30 22:04
 */
public interface SysUserService {

    /**
     * 根据用户名查询用户信息
     *
     * @param userName 用户名
     */
    SysUserDTO getByUserName(String userName);
}
