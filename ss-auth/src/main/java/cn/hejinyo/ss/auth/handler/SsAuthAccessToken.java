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

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.util.Assert;

import java.util.Collections;

/**
 * SsAuthAccessToken
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/4 21:24
 */
@Getter
public class SsAuthAccessToken extends AbstractAuthenticationToken {

    private final String principalType;
    private final String principal;
    private final String credentials;
    /**
     * 客户端详情
     */
    private final RegisteredClient registeredClient;

    public SsAuthAccessToken(RegisteredClient registeredClient, String principalType, String principal, String credentials) {
        super(Collections.emptyList());
        Assert.notNull(registeredClient, "principalType cannot be empty");
        Assert.notNull(principalType, "principalType cannot be empty");
        Assert.notNull(principal, "principal cannot be empty");
        Assert.notNull(credentials, "credentials cannot be empty");
        this.registeredClient = registeredClient;
        this.principalType = principalType;
        this.principal = principal;
        this.credentials = credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }
}
