package cn.hejinyo.ss.auth.controller;

import cn.hejinyo.ss.auth.security.SsAuthUserDetailServiceImpl;
import cn.hejinyo.ss.auth.vo.SsAuthLoginReqVo;
import cn.hejinyo.ss.auth.vo.SsAuthLoginTokenVo;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 登录逻辑
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/3/11
 */
@RestController
@RequestMapping("/v2")
@AllArgsConstructor
public class LoginController {

    private final SsAuthUserDetailServiceImpl userDetailService;

    private final PasswordEncoder passwordEncoder;

    private final JWKSource<SecurityContext> jwkSource;

    @PostMapping("/login")
    public SsAuthLoginTokenVo login(@RequestBody SsAuthLoginReqVo ssAuthLoginReqVo) {
        String username = ssAuthLoginReqVo.getUsername();
        // 根据用户名查询用户
        UserDetails userDetails = userDetailService.loadUserByUsername(username);
        // 对比用户名密码是否正确
        this.additionalAuthenticationChecks(userDetails, ssAuthLoginReqVo);

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
                .subject(username)
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

        SsAuthLoginTokenVo tokenVo = new SsAuthLoginTokenVo();
        tokenVo.setTokenValue(jwtAccessToken.getTokenValue());
        tokenVo.setTokenType(OAuth2AccessToken.TokenType.BEARER.getValue());
        tokenVo.setExpiresIn(jwtAccessToken.getExpiresAt());
        return tokenVo;
    }

    /**
     * 验证用户密码
     */
    private void additionalAuthenticationChecks(UserDetails userDetails, SsAuthLoginReqVo authentication) throws AuthenticationException {
        if (authentication.getPassword() == null) {
            throw new BadCredentialsException("用户密码错误");
        }
        String presentedPassword = authentication.getPassword();
        if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            throw new BadCredentialsException("用户密码错误");
        }
    }
}
