package cn.hejinyo.ss.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/12 22:38
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestApiController {

    private final ConfigurableApplicationContext configurableApplicationContext;

    @Value("${useLocalCache:false}")
    private boolean useLocalCache;

    @RequestMapping("/get")
    public String get() {
        return useLocalCache + "--->" + configurableApplicationContext.getEnvironment().getProperty("useLocalCache");
    }
}
