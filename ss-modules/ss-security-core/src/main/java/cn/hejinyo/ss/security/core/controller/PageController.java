package cn.hejinyo.ss.security.core.controller;

import cn.hejinyo.ss.security.core.dao.SysUserDao;
import cn.hejinyo.ss.security.core.model.SysUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/18 22:18
 */
@RequestMapping("/")
@Controller
public class PageController {

    @GetMapping("/auth/login")
    public String loginPage() {
        return "login";
    }
}