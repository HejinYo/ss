package cn.hejinyo.ss.euraka.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * SpringBoot2.0版本为默认开启了csrf，如果我们现在直接启动Eureka服务的话客户端是注册不上的，所以需要把csrf关闭
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/7/18 21:28
 */
@EnableWebSecurity
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        super.configure(http);
    }
}
