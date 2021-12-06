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

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * 查询客户端和登陆用户名密码
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/6 09:45
 */
@Slf4j
public final class SsAuthClientProvider implements AuthenticationProvider {
    private static final String CLIENT_AUTHENTICATION_ERROR_URI = "https://datatracker.ietf.org/doc/html/draft-ietf-oauth-v2-1-01#section-3.2.1";
    private final RegisteredClientRepository registeredClientRepository;

    /**
     * Constructs an {@code OAuth2ClientAuthenticationProvider} using the provided parameters.
     *
     * @param registeredClientRepository the repository of registered clients
     */
    public SsAuthClientProvider(RegisteredClientRepository registeredClientRepository) {
        Assert.notNull(registeredClientRepository, "registeredClientRepository cannot be null");
        this.registeredClientRepository = registeredClientRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SsAuthClientToken clientAuthentication = (SsAuthClientToken) authentication;
        String clientId = clientAuthentication.getPrincipal().toString();
        // 登陆类型
        String principalType = clientAuthentication.getPrincipalType();
        // 查询客户端
        RegisteredClient registeredClient = this.registeredClientRepository.findByClientId(clientId);
        if (registeredClient == null) {
            throwInvalidClient(OAuth2ParameterNames.CLIENT_ID);
        }
        ClientAuthenticationMethod clientAuthenticationMethod = clientAuthentication.getClientAuthenticationMethod();
        // 验证客户端是不是支持这种方式登陆
        assert registeredClient != null;
        Set<ClientAuthenticationMethod> methodSet = registeredClient.getClientAuthenticationMethods();
        if (methodSet == null || methodSet.stream().noneMatch(clientAuthenticationMethod::equals)) {
            throwInvalidClient("authentication_method");
        }
        // 构建认证成功的token
        return new SsAuthClientToken(clientAuthenticationMethod, clientId, principalType, registeredClient);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SsAuthClientToken.class.isAssignableFrom(authentication);
    }

    private static void throwInvalidClient(String parameterName) {
        OAuth2Error error = new OAuth2Error(
                OAuth2ErrorCodes.INVALID_CLIENT,
                "Ss-auth Client authentication failed: " + parameterName,
                CLIENT_AUTHENTICATION_ERROR_URI);
        throw new OAuth2AuthenticationException(error);
    }
}
