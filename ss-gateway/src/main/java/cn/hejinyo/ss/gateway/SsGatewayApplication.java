package cn.hejinyo.ss.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


/**
 * @author : 贺双双   hejinyo@gmail.com
 * @date : 2021/10/18 23:46
 * SsGatewayApplication
 */
@EnableDiscoveryClient
@SpringBootApplication
public class SsGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsGatewayApplication.class, args);
    }

}
