package cn.hejinyo.ss.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/10/18 23:48
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String helloWorld() {
        return "Hello world";
    }
}
