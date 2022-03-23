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
 * DefaultSecurityConfig
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/11/3 22:27
 */
@EnableWebSecurity
@Slf4j
@RefreshScope
public class AuthorizationServerConfig {

    @Value("${jwt.privateKey}")
    private String privateKeyValue;

    @Value("${jwt.publicKey}")
    private String publicKeyValue;

    @Value("${jwt.kid}")
    private String kidValue;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(req -> {
            // 测试路径
            req.antMatchers("/test/**").permitAll();
            req.antMatchers("/ms/auth/**").permitAll();
            req.antMatchers("/oauth2/jwks").permitAll();
            // 新的登录逻辑测试
            req.antMatchers("/v2/login/**").permitAll();
            // 其他的都需要认证访问
            req.anyRequest().authenticated();
        });

        // 关闭 csrf
        http.csrf().disable();
        return http.build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAPublicKey publicKey = (RSAPublicKey) Utils.getPublicKey(publicKeyValue);
        RSAPrivateKey privateKey = (RSAPrivateKey) Utils.getPrivateKey(privateKeyValue);
        RSAKey rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(kidValue).build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


}
