package cn.hejinyo.ss.auth.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


/**
 * 扫描包的顺序必须这样
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/25 22:49
 */

@EnableFeignClients
@SpringBootApplication
@ComponentScan({"cn.hejinyo.ss.common.redis","cn.hejinyo.ss.common.framework", "cn.hejinyo.ss.auth.server"})
public class SsAuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsAuthServerApplication.class, args);
    }

}
