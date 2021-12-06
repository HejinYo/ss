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
package cn.hejinyo.ss.auth.handler;

import cn.hejinyo.ss.auth.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.JoseHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.util.Assert;

import java.util.HashSet;

/**
 * An {@link AuthenticationProvider} implementation for the OAuth 2.0 Authorization Code Grant.
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/6 09:45
 */
@Slf4j
public class SsAuthAccessTokenProvider implements AuthenticationProvider {
    private final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final ProviderSettings providerSettings;

    public SsAuthAccessTokenProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder,
            ProviderSettings providerSettings) {
        Assert.notNull(jwtEncoder, "registeredClientRepository cannot be null");
        Assert.notNull(userDetailsService, "userDetailsService cannot be null");
        Assert.notNull(passwordEncoder, "passwordEncoder cannot be null");
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.providerSettings = providerSettings;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SsAuthAccessToken authAccessToken = (SsAuthAccessToken) authentication;
        RegisteredClient registeredClient = authAccessToken.getRegisteredClient();
        String username = authAccessToken.getPrincipal().toString();
        String password = authAccessToken.getCredentials().toString();
        // 查询用户
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        UserDetails userDetails = this.retrieveUser(username);
        // 验证用户
        this.additionalAuthenticationChecks(userDetails, authRequest);

        String issuer = this.providerSettings != null ? this.providerSettings.getIssuer() : null;
        JoseHeader.Builder headersBuilder = JwtUtils.headers();
        JwtClaimsSet.Builder claimsBuilder = JwtUtils.accessTokenClaims(registeredClient, issuer, username, new HashSet<>());
        JwtEncodingContext context = JwtEncodingContext.with(headersBuilder, claimsBuilder)
                .registeredClient(registeredClient)
                .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                .authorizationGrant(authAccessToken)
                .build();

        // this.jwtCustomizer.customize(context);

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
            log.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException(this.messages.getMessage("SsAuthClientProvider.badCredentials", "Bad credentials"));
        }
        String presentedPassword = authentication.getCredentials().toString();
        if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            log.debug("Failed to authenticate since password does not match stored value");
            throw new BadCredentialsException(this.messages.getMessage("SsAuthClientProvider.badCredentials", "Bad credentials"));
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
        return SsAuthAccessToken.class.isAssignableFrom(authentication);
    }
}
