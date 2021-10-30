package cn.hejinyo.ss.demo.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/10/18 23:46
 * SsClientApplication
 */
@EnableDiscoveryClient
@SpringBootApplication
public class SsDemoClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsDemoClientApplication.class, args);
    }

}
