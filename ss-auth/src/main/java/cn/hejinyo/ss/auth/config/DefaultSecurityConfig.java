/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.hejinyo.ss.auth.config;

import cn.hejinyo.ss.auth.handler.SsAuthenticationFailureHandler;
import cn.hejinyo.ss.auth.security.UserDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Joe Grandja
 * @since 0.1.0
 */
@EnableWebSecurity
@RequiredArgsConstructor
public class DefaultSecurityConfig {

    /**
     * 配置web访问
     */
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // 登陆的页面和登陆成功跳转页面配置 .loginProcessingUrl()=登陆逻辑的处理路径，默认POST /login
        // .successForwardUrl()=成功转发 .defaultSuccessUrl(url,true)=成功重定向 .successHandler()=自定义成功逻辑处理
        // 登陆失败的路径需要放开无认证访问
        // .failureForwardUrl()=失败转发 .failureUrl()=失败重定向 .failureHandler()=失败的处理器
        // formLogin().usernameParameter() formLogin().passwordParameter() 重写username password参数名称
        http.formLogin().loginPage("/login").failureHandler(new SsAuthenticationFailureHandler("/login", true));

        // 配合权限校验，不认证/认证访问页面
        http.authorizeRequests()
                // .anonymous() 只能匿名访问，登陆后不能访问 .fullyAuthenticated() 完整登陆才能访问
                .antMatchers("/login").anonymous()
                // 可以不认证访问的页面
                .antMatchers("/oauth/authorize").permitAll()
                // 静态资源
                .antMatchers("/favicon.ico", "/css/**", "/img/**").permitAll()
                // 测试路径
                .antMatchers("/test/**").permitAll()
                // 其他的都需要认证访问
                .anyRequest().authenticated();

        // 关闭 csrf
        http.csrf().disable();
        return http.build();
    }

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * 自定义用户查询
     */
    @Bean
    public UserDetailsService users() {
        return new UserDetailServiceImpl();
    }

}
