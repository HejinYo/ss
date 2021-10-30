package cn.hejinyo.ss.demo.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/10/18 23:46
 * SsAuthApplication
 */
@EnableDiscoveryClient
@SpringBootApplication
public class SsDemoAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsDemoAuthApplication.class, args);
    }

}
