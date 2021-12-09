package cn.hejinyo.ss.auth.handler;

import cn.hejinyo.ss.auth.util.OAuth2EndpointUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * SsAuthAccessTokenConverter
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/4 21:24
 */
public class SsAuthLoginConverter implements AuthenticationConverter {
    private static final String DEFAULT_ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1";

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

        // 用户名密码登陆
        if (SsAuthParameterNames.PRINCIPAL_USERNAME.equals(principalType)) {
            String username = parameters.getFirst(SsAuthParameterNames.USERNAME);
            String password = parameters.getFirst(SsAuthParameterNames.PASSWORD);
            if (!StringUtils.hasText(username)) {
                throwError("用户名称不能为空", SsAuthParameterNames.USERNAME);
            }
            if (!StringUtils.hasText(password)) {
                throwError("用户密码不能为空", SsAuthParameterNames.PASSWORD);
            }
            return new SsAuthLoginToken(clientId, principalType, username, password);
        }

        // 手机验证码
        if (SsAuthParameterNames.PRINCIPAL_PHONE.equals(principalType)) {
            String phoneNumber = parameters.getFirst(SsAuthParameterNames.PHONE_NUMBER);
            String phoneCode = parameters.getFirst(SsAuthParameterNames.PHONE_CODE);
            if (!StringUtils.hasText(phoneNumber)) {
                throwError("手机号不能为空", SsAuthParameterNames.PHONE_NUMBER);
            }
            if (!StringUtils.hasText(phoneCode)) {
                throwError("验证码不能为空", SsAuthParameterNames.PHONE_CODE);
            }
            return new SsAuthLoginToken(clientId, principalType, phoneNumber, phoneCode);
        }
        return null;
    }

    private static void throwError(String errorCode, String parameterName) {
        OAuth2Error error = new OAuth2Error(errorCode, "Ss-Auth Client Parameter: " + parameterName,
                DEFAULT_ERROR_URI);
        throw new OAuth2AuthorizationCodeRequestAuthenticationException(error, null);
    }
}
