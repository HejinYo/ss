package cn.hejinyo.ss.cloud.euraka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SsEurakaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsEurakaApplication.class, args);
    }

}
