package cn.hejinyo.ss.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/25 22:49
 */
@SpringBootApplication
@EnableSwagger2
public class SsEsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsEsApplication.class, args);
    }

}
