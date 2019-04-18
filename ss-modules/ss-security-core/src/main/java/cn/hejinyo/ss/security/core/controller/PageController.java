package cn.hejinyo.ss.security.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/18 22:18
 */
@RequestMapping("/")
@Controller
public class PageController {

    @GetMapping("/auth/login")
    public String loginPage(){
        return "login";
    }
}
