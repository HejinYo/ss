package cn.hejinyo.ss.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * SsAuthApplication
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/10/18 23:46
 */
@SpringBootApplication(scanBasePackages = {
        "cn.hejinyo.ss.auth",
        "cn.hejinyo.ss.common.redis"
})
@EnableFeignClients
@EnableDiscoveryClient
public class SsAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsAuthApplication.class, args);
    }

}
