package cn.hejinyo.ss.jelly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * 扫描包的顺序必须这样
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/25 22:49
 */
@EnableFeignClients(basePackages = "cn.hejinyo.ss.auth.jelly")
@ComponentScan({"cn.hejinyo.ss.auth.jelly", "cn.hejinyo.ss.common.shiro.core", "cn.hejinyo.ss.jelly"})
@EnableSwagger2
@SpringBootApplication
public class SsJellyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsJellyApplication.class, args);
    }

}
