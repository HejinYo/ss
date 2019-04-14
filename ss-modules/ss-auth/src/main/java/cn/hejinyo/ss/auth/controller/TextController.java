package cn.hejinyo.ss.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/14 22:11
 */
@RestController
@RequestMapping("/test")
public class TextController {
    @GetMapping
    private String test(){
        return "hello world";
    }
}
