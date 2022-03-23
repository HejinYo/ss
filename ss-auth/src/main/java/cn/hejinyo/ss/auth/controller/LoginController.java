package cn.hejinyo.ss.auth.controller;

import cn.hejinyo.ss.auth.security.SsAuthUserDetailServiceImpl;
import cn.hejinyo.ss.auth.util.RedisKeys;
import cn.hejinyo.ss.auth.util.RedisUtils;
import cn.hejinyo.ss.auth.vo.SsAuthLoginReqVo;
import cn.hejinyo.ss.auth.vo.SsAuthLoginTokenVo;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 登录逻辑
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/3/11
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class LoginController {

    private final SsAuthUserDetailServiceImpl userDetailService;

    private final PasswordEncoder passwordEncoder;

    private final JWKSource<SecurityContext> jwkSource;

    private final RedisUtils redisUtils;

    @PostMapping("/v2/login")
    public SsAuthLoginTokenVo login(@RequestBody SsAuthLoginReqVo ssAuthLoginReqVo) {
        String username = ssAuthLoginReqVo.getUsername();
        // 根据用户名查询用户
        UserDetails userDetails = userDetailService.loadUserByUsername(username);
        // 对比用户名密码是否正确
        this.additionalAuthenticationChecks(userDetails, ssAuthLoginReqVo);
        // JWT 头部
        JwsHeader.Builder headersBuilder = JwsHeader.with(SignatureAlgorithm.RS256);
        JwsHeader headers = headersBuilder.build();
        // 签发时间
        Instant issuedAt = Instant.now();
        // 有效时间
        Instant expiresAt = issuedAt.plus(Duration.ofMinutes(5));
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
        JwtClaimsSet claims = claimsBuilder.build();
        JwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource);
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(headers, claims);
        Jwt jwtAccessToken = jwtEncoder.encode(jwtEncoderParameters);
        String tokenId = UUID.randomUUID().toString();
        // redis 时间比实际token超时时间少5s
        long expiresSeconds = Duration.between(new Date().toInstant(), expiresAt).getSeconds() - 5;
        redisUtils.setEx(RedisKeys.USER_TOKEN + tokenId, jwtAccessToken.getTokenValue(), expiresSeconds);
        SsAuthLoginTokenVo tokenVo = new SsAuthLoginTokenVo();
        tokenVo.setTokenValue(tokenId);
        tokenVo.setTokenType("ss");
        tokenVo.setExpiresIn(jwtAccessToken.getExpiresAt());
        return tokenVo;
    }

    private void additionalAuthenticationChecks(UserDetails userDetails, SsAuthLoginReqVo authentication) throws AuthenticationException {
        if (authentication.getPassword() == null) {
            throw new BadCredentialsException("用户密码错误");
        }
        String presentedPassword = authentication.getPassword();
        if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            throw new BadCredentialsException("用户密码错误");
        }
    }

    @GetMapping(value = "/oauth2/jwks", produces = MediaType.APPLICATION_JSON_VALUE)
    public String jwks() {
        try {
            JWKSelector selector = new JWKSelector(new JWKMatcher.Builder().build());
            return new JWKSet(this.jwkSource.get(selector, null)).toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to select the JWK(s) -> " + ex.getMessage(), ex);
        }
    }

}
