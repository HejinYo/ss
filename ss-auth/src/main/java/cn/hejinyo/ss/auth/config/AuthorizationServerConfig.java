package cn.hejinyo.ss.auth.config;

import cn.hejinyo.ss.common.core.util.Utils;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;


/**
 * AuthorizationServerConfig
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/11/3 22:27
 */
@Slf4j
@RefreshScope
@EnableWebSecurity
public class AuthorizationServerConfig {

    /**
     * jwt 私钥
     */
    @Value("${login.token.pri}")
    private String pri;

    /**
     * jwt 公钥
     */
    @Value("${login.token.pub}")
    private String pub;

    /**
     * jwt 密钥 ID 可用于匹配特定密钥
     */
    @Value("${login.token.kid}")
    private String kid;

    /**
     * 授权服务器的过滤器链
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests(req -> {
            // 微服务验证msToken
            req.antMatchers("/ms/auth/jwkSet").permitAll();
            // 网关验证accessToken
            req.antMatchers("/ms/auth/checkAndGetMsToken").permitAll();
            // 登录逻辑
            req.antMatchers("/api/auth/login").permitAll();
            // 测试路径
            req.antMatchers("/api/test/**").permitAll();
            // 其他的都需要认证访问
            req.anyRequest().authenticated();
        });

        // 关闭 csrf
        http.csrf().disable();
        return http.build();
    }

    /**
     * JSON Web 密钥 (JWK) 源。 公开用于检索与指定选择器匹配的 JWK 的方法
     *
     * @return JWKSource<SecurityContext>
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAPublicKey publicKey = (RSAPublicKey) Utils.getPublicKey(pub);
        RSAPrivateKey privateKey = (RSAPrivateKey) Utils.getPrivateKey(pri);
        RSAKey rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(kid).build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    /**
     * 用于编码密码的服务，推荐使用BCryptPasswordEncoder
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


}
