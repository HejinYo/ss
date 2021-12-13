package cn.hejinyo.ss.auth.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.jwt.JoseHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;


/**
 * SsAuth 登陆处理
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/11/3 22:27
 */
@Slf4j
public class SsAuthLoginProvider implements AuthenticationProvider {
    private OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final ProviderSettings providerSettings;
    private final RegisteredClientRepository registeredClientRepository;

    public SsAuthLoginProvider(
            RegisteredClientRepository registeredClientRepository,
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder,
            ProviderSettings providerSettings) {
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        Assert.notNull(userDetailsService, "userDetailsService cannot be null");
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        Assert.notNull(jwtEncoder, "registeredClientRepository cannot be null");
        this.registeredClientRepository = registeredClientRepository;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.providerSettings = providerSettings;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SsAuthLoginToken authAccessToken = (SsAuthLoginToken) authentication;
        String clientId = authAccessToken.getClientId();
        String username = authAccessToken.getPrincipal().toString();
        String password = authAccessToken.getCredentials().toString();
        // 查询客户端
        RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
        if (registeredClient == null) {
            throw new SsAuthException("authentication failed: clientId");
        }
        // 验证客户端支持登陆方式
        Set<ClientAuthenticationMethod> methodSet = registeredClient.getClientAuthenticationMethods();
        if (methodSet == null || methodSet.stream().noneMatch(ClientAuthenticationMethod.NONE::equals)) {
            throw new SsAuthException("authentication failed: authentication_method");
        }

        // 查询用户
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        UserDetails userDetails = this.retrieveUser(username);
        // 验证用户
        this.additionalAuthenticationChecks(userDetails, authRequest);

        String issuer = this.providerSettings != null ? this.providerSettings.getIssuer() : null;
        JoseHeader.Builder headersBuilder = SsAuthServerJwtUtils.headers();
        JwtClaimsSet.Builder claimsBuilder = SsAuthServerJwtUtils.accessTokenClaims(registeredClient, issuer, username, new HashSet<>());
        JwtEncodingContext context = JwtEncodingContext.with(headersBuilder, claimsBuilder)
                .registeredClient(registeredClient)
                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                .authorizationGrant(authAccessToken)
                .build();

        if (this.jwtCustomizer != null) {
            this.jwtCustomizer.customize(context);
        }

        JoseHeader headers = context.getHeaders().build();
        JwtClaimsSet claims = context.getClaims().build();
        Jwt jwtAccessToken = this.jwtEncoder.encode(headers, claims);
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                jwtAccessToken.getTokenValue(), jwtAccessToken.getIssuedAt(),
                jwtAccessToken.getExpiresAt(), new HashSet<>());
        return new OAuth2AccessTokenAuthenticationToken(registeredClient, authAccessToken, accessToken);
    }


    /**
     * 验证用户密码
     */
    private void additionalAuthenticationChecks(UserDetails userDetails,
                                                UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException("用户密码错误");
        }
        String presentedPassword = authentication.getCredentials().toString();
        if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            throw new BadCredentialsException("用户密码错误");
        }
    }

    /**
     * 查询用户
     */
    private UserDetails retrieveUser(String username) throws AuthenticationException {
        try {
            UserDetails loadedUser = this.getUserDetailsService().loadUserByUsername(username);
            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
            }
            return loadedUser;
        } catch (UsernameNotFoundException | InternalAuthenticationServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    private UserDetailsService getUserDetailsService() {
        return this.userDetailsService;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return SsAuthLoginToken.class.isAssignableFrom(authentication);
    }

    public void setJwtCustomizer(OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer) {
        this.jwtCustomizer = jwtCustomizer;
    }
}
