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
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Transient;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.Version;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.Collections;

/**
 * SsAuthClientToken
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/6 09:45
 */
@Transient
public class SsAuthClientToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = Version.SERIAL_VERSION_UID;

    @Getter
    private final ClientAuthenticationMethod clientAuthenticationMethod;

    @Getter
    private final String principalType;

    private final String principal;

    public SsAuthClientToken(ClientAuthenticationMethod clientAuthenticationMethod,
                             String clientId, String principalType,
                             @Nullable RegisteredClient registeredClient) {
        super(Collections.emptyList());
        this.clientAuthenticationMethod = clientAuthenticationMethod;
        this.principalType = principalType;
        this.principal = clientId;
        super.setDetails(registeredClient);
        this.setAuthenticated(registeredClient != null);
    }

    @Override
    public Object getDetails() {
        return super.getDetails();
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

}
