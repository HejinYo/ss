package cn.hejinyo.ss.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * SsAdminApplication
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/10/18 23:46
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class SsAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsAdminApplication.class, args);
    }

}
