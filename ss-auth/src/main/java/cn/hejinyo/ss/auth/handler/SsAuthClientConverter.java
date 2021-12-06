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

import cn.hejinyo.ss.auth.util.OAuth2EndpointUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 请求参数中获取 client 和 用户登陆信息
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/6 09:45
 */
public final class SsAuthClientConverter implements AuthenticationConverter {

    @Nullable
    @Override
    public Authentication convert(HttpServletRequest request) {

        MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);

        // client_id (REQUIRED)
        String clientId = parameters.getFirst(SsAuthParameterNames.CLIENT_ID);
        if (!StringUtils.hasText(clientId)) {
            return null;
        }

        // principal_type (REQUIRED)
        String principalType = parameters.getFirst(SsAuthParameterNames.PRINCIPAL_TYPE);
        if (!StringUtils.hasText(principalType)) {
            return null;
        }

        // grant_type (REQUIRED)
        String grantType = parameters.getFirst(SsAuthParameterNames.GRANT_TYPE);
        // 只处理 grantType = password
        if (!StringUtils.hasText(grantType) || !AuthorizationGrantType.PASSWORD.getValue().equals(grantType)) {
            return null;
        }

        return new SsAuthClientToken(ClientAuthenticationMethod.NONE, clientId, principalType, null);

    }
}
