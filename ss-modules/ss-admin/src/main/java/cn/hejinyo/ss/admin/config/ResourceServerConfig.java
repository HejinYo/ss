package cn.hejinyo.ss.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Joe Grandja
 * @since 0.0.1
 */
@EnableWebSecurity
public class ResourceServerConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.mvcMatcher("/messages/**")
//                .authorizeRequests()
//                .mvcMatchers("/messages/**").access("hasAuthority('SCOPE_user.userInfo')")
//                .and()
//                .oauth2ResourceServer()
//                .jwt();
        http.authorizeRequests().antMatchers("/test/**").permitAll()
                .mvcMatchers("/messages/**").access("hasAuthority('SCOPE_user.userInfo')");
        return http.build();
    }

}
