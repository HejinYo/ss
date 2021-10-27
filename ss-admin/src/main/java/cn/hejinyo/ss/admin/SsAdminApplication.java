package cn.hejinyo.ss.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;


/**
 * @author : 贺双双   hejinyo@gmail.com
 * @date : 2021/10/18 23:46
 * SsAdminApplication
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableResourceServer
public class SsAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsAdminApplication.class, args);
    }

}
