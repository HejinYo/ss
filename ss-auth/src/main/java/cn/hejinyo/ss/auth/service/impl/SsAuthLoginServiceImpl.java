package cn.hejinyo.ss.auth.service.impl;

import cn.hejinyo.ss.auth.service.SsAuthLoginService;
import cn.hejinyo.ss.auth.util.RedisKeys;
import cn.hejinyo.ss.auth.vo.SsAuthLoginReqVo;
import cn.hejinyo.ss.auth.vo.SsAuthLoginTokenVo;
import cn.hejinyo.ss.common.redis.util.RedisUtils;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 用户登陆实现
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/3/27 18:50
 */
@Service
@RequiredArgsConstructor
public class SsAuthLoginServiceImpl implements SsAuthLoginService {

    private final SsAuthUserDetailServiceImpl userDetailService;

    private final PasswordEncoder passwordEncoder;

    private final JWKSource<SecurityContext> jwkSource;

    private final RedisUtils redisUtils;

    /**
     * 用户登陆返回token
     */
    @Override
    public SsAuthLoginTokenVo login(SsAuthLoginReqVo ssAuthLoginReqVo) {
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
        tokenVo.setTokenType("ss_web");
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

    /**
     * 微服务获取 jwk
     */
    @Override
    public String jwkSet() {
        try {
            JWKSelector selector = new JWKSelector(new JWKMatcher.Builder().build());
            return new JWKSet(this.jwkSource.get(selector, null)).toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to select the JWK(s) -> " + ex.getMessage(), ex);
        }
    }


    /**
     * 验证是否登陆并换取微服务 MsToken
     */
    @Override
    public String checkAndGetMsToken(String accessToken) {
        String token = redisUtils.get(RedisKeys.USER_TOKEN + accessToken);
        if (StringUtils.hasText(token)) {
            return token;
        }
        // token过期，需要重新登陆
        return null;
    }
}
