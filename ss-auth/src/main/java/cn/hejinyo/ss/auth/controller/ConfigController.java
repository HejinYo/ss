package cn.hejinyo.ss.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/2 20:21
 */
@RestController
@RequestMapping("/test/config")
@RefreshScope
public class ConfigController {
    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;

    @Value("${useLocalCache:false}")
    private boolean useLocalCache;

    @RequestMapping("/get")
    public String get() {
        return useLocalCache + "--->" + configurableApplicationContext.getEnvironment().getProperty("useLocalCache");
    }
}
