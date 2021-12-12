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
package cn.hejinyo.ss.auth.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.Assert;

import java.util.Collections;

/**
 * SsAuth 登陆参数令牌
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/11/3 22:27
 */
@Getter
public class SsAuthLoginToken extends AbstractAuthenticationToken {

    private final String clientId;
    private final String principalType;
    private final String principal;
    private final String credentials;

    public SsAuthLoginToken(String clientId, String principalType, String principal, String credentials) {
        super(Collections.emptyList());
        Assert.notNull(clientId, "clientId cannot be empty");
        Assert.notNull(principalType, "principalType cannot be empty");
        Assert.notNull(principal, "principal cannot be empty");
        Assert.notNull(credentials, "credentials cannot be empty");
        this.clientId = clientId;
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
