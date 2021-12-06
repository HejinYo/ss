package cn.hejinyo.ss.auth.config;

import cn.hejinyo.ss.auth.handler.*;
import cn.hejinyo.ss.auth.util.Utils;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwsEncoder;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;


/**
 * AuthorizationServerConfig
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/11/3 22:27
 */
@Configuration
@Slf4j
@RefreshScope
public class AuthorizationServerConfig {

    @Value("${jwt.privateKey}")
    private String privateKeyValue;

    @Value("${jwt.publicKey}")
    private String publicKeyValue;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      RegisteredClientRepository registeredClientRepository,
                                                                      UserDetailsService userDetailsService,
                                                                      PasswordEncoder passwordEncoder,
                                                                      JWKSource<SecurityContext> jwkSource) throws Exception {
        // 授权服务器配置
        OAuth2AuthorizationServerConfigurer<HttpSecurity> configurer = new OAuth2AuthorizationServerConfigurer<>();
        // 认证端点
        configurer.authorizationEndpoint(config -> config.errorResponseHandler(new SsAuthFailureRestHandler("用户认证失败")));
        // 客户端认证
        configurer.clientAuthentication(config -> {
            config.authenticationConverter(new SsAuthClientConverter());
            config.authenticationProvider(new SsAuthClientProvider(registeredClientRepository));
            config.errorResponseHandler(new SsAuthFailureRestHandler("获取client失败"));
        });
        // 令牌端点
        configurer.tokenEndpoint(config -> {
            config.accessTokenRequestConverter(new SsAuthAccessTokenConverter());
            config.authenticationProvider(new SsAuthAccessTokenProvider(userDetailsService, passwordEncoder,
                    new NimbusJwsEncoder(jwkSource), providerSettings()));
            config.accessTokenResponseHandler((new SsAuthAccessTokenRespHandler()));
            config.errorResponseHandler(new SsAuthFailureRestHandler("获取token失败"));
        });

        RequestMatcher endpointsMatcher = configurer.getEndpointsMatcher();
        return http
                .requestMatcher(endpointsMatcher)
                .authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .apply(configurer)
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
                .clientId("SS-WEB")
                // 客户端密码，这里不需要加密，保存的时候自动加密了
                .clientSecret("123456")
                // 自定义模式的方式需要 NONE 和授权服务器进行认证
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                // 密码模式
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                // 客户端申请的作用域
                .scope("ss:auth")
                .scope("ss:admin")
                .tokenSettings(TokenSettings.builder()
                        // accessToken 的有效期
                        .accessTokenTimeToLive(Duration.ofDays(1))
                        // 是否可重用刷新令牌
                        .reuseRefreshTokens(false).build())
                .build();
        JdbcRegisteredClientRepository jdbcRegisteredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
        if (null == jdbcRegisteredClientRepository.findByClientId("SS-WEB")) {
            jdbcRegisteredClientRepository.save(registeredClient);
        }
        return jdbcRegisteredClientRepository;
    }

    /**
     * 对JWT进行签名的 加解密密钥
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAPublicKey publicKey = (RSAPublicKey) Utils.getPublicKey(publicKeyValue);
        RSAPrivateKey privateKey = (RSAPrivateKey) Utils.getPrivateKey(privateKeyValue);
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
                // 发布者的url地址,一般是本系统访问的根路径
                .issuer("http://m.hejinyo.cn").build();
    }

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Configuration
    static class MvcConfig implements WebMvcConfigurer {
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("login").setViewName("login");
        }
    }
}
