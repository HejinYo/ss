package cn.hejinyo.ss.security.core.config;

import cn.hejinyo.ss.security.core.filter.BaseFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/18 23:04
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private BaseAuthenticationSuccessHandler baseAuthenticationSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.addFilterBefore(new BaseFilter(), UsernamePasswordAuthenticationFilter.class)
                .formLogin()
                // 指定登录页面
                .loginPage("/auth/login")
                .loginProcessingUrl("/authentication/form")
                .successHandler(baseAuthenticationSuccessHandler)
                .and()
                // 定义授权配置
                .authorizeRequests()
                // 访问指定url不需要认证
                .antMatchers("/auth/login").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/img/**").permitAll()
                .antMatchers("/favicon.ico").permitAll()
                // 任何请求
                .anyRequest()
                // 需要身份认证
                .authenticated()
                .and()
                .csrf().disable();

    }

   /* @Override
    public void configure(WebSecurity web) throws Exception {
        // 关闭spring security
        web.ignoring().antMatchers("/**");
    }*/
}
