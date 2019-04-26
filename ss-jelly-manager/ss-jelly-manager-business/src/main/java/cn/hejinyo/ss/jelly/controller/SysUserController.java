package cn.hejinyo.ss.jelly.controller;

import cn.hejinyo.ss.jelly.dao.SysUserDao;
import cn.hejinyo.ss.jelly.entity.SysUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/19 23:08
 */
@RequestMapping("/")
@RestController
public class SysUserController {
    @Autowired
    private SysUserDao sysUserDao;

    @GetMapping("/user/{id}")
    public SysUserEntity getById(@PathVariable("id") Integer id) {
        return sysUserDao.getOne(id);
    }
}

