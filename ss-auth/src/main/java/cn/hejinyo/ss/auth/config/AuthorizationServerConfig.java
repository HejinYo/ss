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

import cn.hejinyo.ss.auth.security.Ss0auth2AuthorizationServiceImpl;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.web.OAuth2ClientAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

/**
 * @author Joe Grandja
 * @since 0.0.1
 */
@Configuration
@Slf4j
public class AuthorizationServerConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 个性化 JWT token
     */
    class CustomOAuth2TokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

        @Override
        public void customize(JwtEncodingContext context) {
            // 添加一个自定义头
            context.getHeaders().header("client-id", context.getRegisteredClient().getClientId());
        }
    }

    /**
     * 定义 Spring Security 的拦截器链，比如我们的 授权url、获取token的url 需要由那个过滤器来处理，此处配置这个。
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

        // 设置jwt token个性化
        http.setSharedObject(OAuth2TokenCustomizer.class, new CustomOAuth2TokenCustomizer());

        // 授权服务器配置
        OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer<>();

        // 授权端点
        authorizationServerConfigurer.authorizationEndpoint(config -> {
            config.errorResponseHandler((request, response, exception) -> {
                OAuth2AuthenticationException oAuth2AuthenticationException = (OAuth2AuthenticationException) exception;
                OAuth2Error error = oAuth2AuthenticationException.getError();
                log.error("错误原因:[{}]", error);

                log.info("认证异常", exception);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setContentType(MediaType.APPLICATION_JSON.toString());
                response.getWriter().write("{\"code\":-1,\"msg\":\"认证失败\"}");
            });

        });

        // token 端点
        authorizationServerConfigurer.tokenEndpoint(config -> {
            config.errorResponseHandler((request, response, exception) -> {
                OAuth2AuthenticationException oAuth2AuthenticationException = (OAuth2AuthenticationException) exception;
                OAuth2Error error = oAuth2AuthenticationException.getError();
                log.error("错误原因:[{}]", error);

                log.info("认证异常", exception);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setContentType(MediaType.APPLICATION_JSON.toString());
                response.getWriter().write("{\"code\":-1,\"msg\":\"认证失败\"}");
            });
        });

        authorizationServerConfigurer.addObjectPostProcessor(new ObjectPostProcessor<Object>() {
            @Override
            public <O> O postProcess(O object) {
                if (object instanceof OAuth2ClientAuthenticationFilter) {
                    OAuth2ClientAuthenticationFilter filter = (OAuth2ClientAuthenticationFilter) object;
                    filter.setAuthenticationFailureHandler((request, response, exception) -> {
                        OAuth2AuthenticationException oAuth2AuthenticationException = (OAuth2AuthenticationException) exception;
                        OAuth2Error oAuth2Error = oAuth2AuthenticationException.getError();
                        switch (oAuth2Error.getErrorCode()) {
                            case OAuth2ErrorCodes.INVALID_CLIENT:
                                log.info("未知的客户端");
                                break;
                            case OAuth2ErrorCodes.ACCESS_DENIED:
                                log.info("您无权限访问");
                                break;
                            default:
                                break;
                        }
                        log.error("错误原因:[{}]", oAuth2Error);

                        log.info("认证异常", exception);
                        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                        response.setContentType(MediaType.APPLICATION_JSON.toString());
                        response.getWriter().write("{\"code\":-2,\"msg\":\"认证失败\"}");
                    });
                }
                return object;
            }
        });

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();

        return http
                .requestMatcher(endpointsMatcher)
                .authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .apply(authorizationServerConfigurer)
                .and()
                .formLogin()
                .and()
                .build();
    }

    /**
     * 创建客户端信息，可以保存在内存和数据库，此处保存在数据库中
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                // 客户端id 需要唯一
                .clientId("csdn")
                // 客户端密码，这里不需要加密，保存的时候自动加密了
                .clientSecret("csdn123")
                // 可以基于 basic 的方式和授权服务器进行认证
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                // 授权码
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                // 刷新token
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                // 客户端模式
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                // 密码模式,已经不支持了
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                // 重定向url
                .redirectUri("http://localhost:9010/login/code")
                // 客户端申请的作用域，也可以理解这个客户端申请访问用户的哪些信息，比如：获取用户信息，获取用户照片等
                .scope("user.userInfo")
                .scope("user.photos")
//                .clientSettings(clientSettings -> {
//                    // 是否需要用户确认一下客户端需要获取用户的哪些权限
//                    // 比如：客户端需要获取用户的 用户信息、用户照片 但是此处用户可以控制只给客户端授权获取 用户信息。
//                    clientSettings.requireUserConsent(true);
//                })
//                .tokenSettings(tokenSettings -> {
//                    // accessToken 的有效期
//                    tokenSettings.accessTokenTimeToLive(Duration.ofHours(1));
//                    // refreshToken 的有效期
//                    tokenSettings.refreshTokenTimeToLive(Duration.ofDays(3));
//                    // 是否可重用刷新令牌
//                    tokenSettings.reuseRefreshTokens(true);
//                })
                .build();

        JdbcRegisteredClientRepository jdbcRegisteredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
        if (null == jdbcRegisteredClientRepository.findByClientId("csdn")) {
            jdbcRegisteredClientRepository.save(registeredClient);
        }
        return jdbcRegisteredClientRepository;
    }

    /**
     * 保存授权信息，授权服务器给我们颁发来token，那我们肯定需要保存吧，由这个服务来保存
     */
    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
//        JdbcOAuth2AuthorizationService authorizationService = new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
//
//        class CustomOAuth2AuthorizationRowMapper extends JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper {
//            public CustomOAuth2AuthorizationRowMapper(RegisteredClientRepository registeredClientRepository) {
//                super(registeredClientRepository);
//                getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//                this.setLobHandler(new DefaultLobHandler());
//            }
//        }
//
//        CustomOAuth2AuthorizationRowMapper oAuth2AuthorizationRowMapper =
//                new CustomOAuth2AuthorizationRowMapper(registeredClientRepository);
//
//        authorizationService.setAuthorizationRowMapper(oAuth2AuthorizationRowMapper);
//        return authorizationService;
        return new Ss0auth2AuthorizationServiceImpl();
    }

    /**
     * 如果是授权码的流程，可能客户端申请了多个权限，比如：获取用户信息，修改用户信息，此Service处理的是用户给这个客户端哪些权限，比如只给获取用户信息的权限
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate,
                                                                         RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }

    /**
     * 对JWT进行签名的 加解密密钥
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    /**
     * jwt 解码
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * 配置一些断点的路径，比如：获取token、授权端点 等
     */
    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder()
                // 配置获取token的端点路径
                .tokenEndpoint("/oauth2/token")
                // 发布者的url地址,一般是本系统访问的根路径
                // 此处的 qq.com 需要修改我们系统的 host 文件
                .issuer("http://hejinyo.com:9001").build();
    }

    @Configuration
    static class MvcConfig implements WebMvcConfigurer {
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("login").setViewName("login");
        }
    }
}
