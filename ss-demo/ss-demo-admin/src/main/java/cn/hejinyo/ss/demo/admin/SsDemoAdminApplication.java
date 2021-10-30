package cn.hejinyo.ss.demo.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;


/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/10/18 23:46
 * SsAdminApplication
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableResourceServer
public class SsDemoAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsDemoAdminApplication.class, args);
    }

}
