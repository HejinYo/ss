package cn.hejinyo.ss.auth.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * 扫描包的顺序必须这样
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/25 22:49
 */

@EnableFeignClients
@EnableSwagger2
@SpringBootApplication
public class SsAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsAuthServerApplication.class, args);
    }

}
