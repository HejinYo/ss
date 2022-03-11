package cn.hejinyo.ss.auth.controller;

import cn.hejinyo.ss.auth.security.SsAuthLoginToken;
import cn.hejinyo.ss.auth.security.SsAuthServerConstant;
import cn.hejinyo.ss.auth.security.SsAuthServerJwtUtils;
import cn.hejinyo.ss.auth.security.SsAuthUserDetailServiceImpl;
import cn.hejinyo.ss.auth.vo.LoginReqVo;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;

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
    private final ProviderSettings providerSettings;
    private final JWKSource<SecurityContext> jwkSource;
    private final RegisteredClientRepository registeredClientRepository;

    @PostMapping("/login")
    public OAuth2AccessToken login(@RequestBody LoginReqVo loginReqVo) {
        String username = loginReqVo.getUsername();
        // 根据用户名查询用户
        UserDetails userDetails = userDetailService.loadUserByUsername(username);
        // 对比用户名密码是否正确
        // 验证用户
        this.additionalAuthenticationChecks(userDetails, loginReqVo);
        // 生成token
        String issuer = this.providerSettings != null ? this.providerSettings.getIssuer() : null;
        JoseHeader.Builder headersBuilder = SsAuthServerJwtUtils.headers();
        // 查询客户端
        RegisteredClient registeredClient = this.registeredClientRepository.findByClientId("SS-WEB");
        assert registeredClient != null;
        JwtClaimsSet.Builder claimsBuilder = SsAuthServerJwtUtils.accessTokenClaims(registeredClient, issuer, username, new HashSet<>());

        SsAuthLoginToken authAccessToken = new SsAuthLoginToken("SS-WEB", SsAuthServerConstant.PRINCIPAL_USERNAME, loginReqVo.getUsername(), loginReqVo.getPassword());
        JwtEncodingContext context = JwtEncodingContext.with(headersBuilder, claimsBuilder).registeredClient(registeredClient).tokenType(OAuth2TokenType.ACCESS_TOKEN).authorizationGrantType(AuthorizationGrantType.PASSWORD).authorizationGrant(authAccessToken).build();

        JoseHeader headers = context.getHeaders().build();
        JwtClaimsSet claims = context.getClaims().build();

        JwtEncoder jwtEncoder = new NimbusJwsEncoder(jwkSource);
        Jwt jwtAccessToken = jwtEncoder.encode(headers, claims);
        return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, jwtAccessToken.getTokenValue(), jwtAccessToken.getIssuedAt(), jwtAccessToken.getExpiresAt(), new HashSet<>());
    }

    /**
     * 验证用户密码
     */
    private void additionalAuthenticationChecks(UserDetails userDetails, LoginReqVo authentication) throws AuthenticationException {
        if (authentication.getPassword() == null) {
            throw new BadCredentialsException("用户密码错误");
        }
        String presentedPassword = authentication.getPassword();
        if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            throw new BadCredentialsException("用户密码错误");
        }
    }
}
