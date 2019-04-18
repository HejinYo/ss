package cn.hejinyo.ss.security.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/18 23:04
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .and()
                // 定义授权配置
                .authorizeRequests()
                // 任何请求
                .anyRequest()
                // 需要身份认证
                .authenticated();

    }

   /* @Override
    public void configure(WebSecurity web) throws Exception {
        // 关闭spring security
        web.ignoring().antMatchers("/**");
    }*/
}
