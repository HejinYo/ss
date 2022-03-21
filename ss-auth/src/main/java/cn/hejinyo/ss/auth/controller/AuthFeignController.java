package cn.hejinyo.ss.auth.controller;

import cn.hejinyo.ss.common.core.util.Utils;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/20 20:34
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ms/auth/")
public class AuthFeignController {

    private final JWKSource<SecurityContext> jwkSource;

    @Value("${jwt.publicKey}")
    private String publicKeyValue;

    /**
     * 通过访问token获取业务token
     *
     * @param accessToken 访问token
     * @return String
     */
    @PostMapping("/getMsToken")
    public String getMsToken(@RequestBody String accessToken) {
        RSAPublicKey publicKey = (RSAPublicKey) Utils.getPublicKey(publicKeyValue);

        JwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(publicKey).build();
        Jwt jwt = jwtDecoder.decode(accessToken.replace("Bearer ",""));

        // 解析token，签发微服务访问token TODO
        // JWT 头部
        JoseHeader.Builder headersBuilder = JoseHeader.withAlgorithm(SignatureAlgorithm.RS256);
        // 签发时间
        Instant issuedAt = Instant.now();
        // 有效时间
        Instant expiresAt = issuedAt.plus(Duration.ofDays(7));
        // JWT 声明
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder();
        claimsBuilder
                // 签发者
                .issuer("http://m.hejinyo.cn")
                // 主体
                .subject(jwt.getSubject())
                // 接收者
                .audience(Collections.singletonList("SS-WEB"))
                // 签发时间
                .issuedAt(issuedAt)
                // 有效时间
                .expiresAt(expiresAt)
                // 之前无效
                .notBefore(issuedAt);
        // 用户权限
        Set<String> scopes = new HashSet<>(Arrays.asList("sys:user:create", "ROLE_admin"));
        claimsBuilder.claim(OAuth2ParameterNames.SCOPE, scopes);
        JoseHeader headers = headersBuilder.build();
        JwtClaimsSet claims = claimsBuilder.build();
        JwtEncoder jwtEncoder = new NimbusJwsEncoder(jwkSource);
        Jwt jwtAccessToken = jwtEncoder.encode(headers, claims);
        return jwtAccessToken.getTokenValue();
    }

}
