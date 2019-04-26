package cn.hejinyo.ss.jelly.api;

import cn.hejinyo.ss.jelly.dto.SysUserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/04/27 00:57
 */

@RequestMapping("/")
public interface SysUserApiService {

    /**
     * 获取用户信息
     */
    @GetMapping("/info/{userId}")
    SysUserDTO getUserInfo(@PathVariable Integer userId);
}
