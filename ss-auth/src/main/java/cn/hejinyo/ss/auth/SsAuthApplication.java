package cn.hejinyo.ss.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;


/**
 * @author : 贺双双   hejinyo@gmail.com
 * @date : 2021/10/18 23:46
 * SsAuthApplication
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableResourceServer
public class SsAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsAuthApplication.class, args);
    }

}
