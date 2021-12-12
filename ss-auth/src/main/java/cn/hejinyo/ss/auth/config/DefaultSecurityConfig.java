package cn.hejinyo.ss.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * DefaultSecurityConfig
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/11/3 22:27
 */
@EnableWebSecurity
public class DefaultSecurityConfig {

    /**
     * 配置web访问
     */
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests(req -> {
            // 测试路径
            req.antMatchers("/test/**").permitAll();
            // 其他的都需要认证访问
            req.anyRequest().authenticated();
        });

        // 关闭 csrf
        http.csrf().disable();
        return http.build();
    }

}
