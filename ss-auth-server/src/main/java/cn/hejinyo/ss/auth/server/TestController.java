package cn.hejinyo.ss.auth.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/04/27 01:09
 */
@RestController
@RequestMapping
public class TestController {
    @Autowired
    private TestRefactorSysUserService testRefactorSysUserService;

    @GetMapping("/user/inf/{userId}")
    public Object info(@PathVariable Integer userId) {
        return testRefactorSysUserService.getUserInfo(userId);
    }
}
