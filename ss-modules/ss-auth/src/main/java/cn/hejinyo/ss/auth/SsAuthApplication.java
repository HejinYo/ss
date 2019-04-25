package cn.hejinyo.ss.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * 扫描包的顺序必须这样
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/25 22:49
 */
@ComponentScan({"cn.hejinyo.ss.auth", "cn.hejinyo.ss.common.shiro.core"})
@EnableSwagger2
@SpringBootApplication
public class SsAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsAuthApplication.class, args);
    }

}
