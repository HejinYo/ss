package cn.hejinyo.ss.auth.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * SsAuth 登陆参数转换成令牌
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/11/3 22:27
 */
public class SsAuthLoginConverter implements AuthenticationConverter {
    private static final String DEFAULT_ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-4.1.2.1";

    @Override
    public Authentication convert(HttpServletRequest request) {
        MultiValueMap<String, String> parameters = SsAuthServerUtils.getParameters(request);

        // client_id (REQUIRED)
        String clientId = parameters.getFirst(SsAuthServerConstant.CLIENT_ID);
        if (!StringUtils.hasText(clientId)) {
            return null;
        }

        // principal_type (REQUIRED)
        String principalType = parameters.getFirst(SsAuthServerConstant.PRINCIPAL_TYPE);
        if (!StringUtils.hasText(principalType)) {
            return null;
        }

        // grant_type (REQUIRED)
        String grantType = parameters.getFirst(SsAuthServerConstant.GRANT_TYPE);
        // 只处理 grantType = password
        if (!StringUtils.hasText(grantType) || !AuthorizationGrantType.PASSWORD.getValue().equals(grantType)) {
            return null;
        }

        // 用户名密码登陆
        if (SsAuthServerConstant.PRINCIPAL_USERNAME.equals(principalType)) {
            String username = parameters.getFirst(SsAuthServerConstant.USERNAME);
            String password = parameters.getFirst(SsAuthServerConstant.PASSWORD);
            if (!StringUtils.hasText(username)) {
                throwError("用户名称不能为空", SsAuthServerConstant.USERNAME);
            }
            if (!StringUtils.hasText(password)) {
                throwError("用户密码不能为空", SsAuthServerConstant.PASSWORD);
            }
            return new SsAuthLoginToken(clientId, principalType, username, password);
        }

        // 手机验证码
        if (SsAuthServerConstant.PRINCIPAL_PHONE.equals(principalType)) {
            String phoneNumber = parameters.getFirst(SsAuthServerConstant.PHONE_NUMBER);
            String phoneCode = parameters.getFirst(SsAuthServerConstant.PHONE_CODE);
            if (!StringUtils.hasText(phoneNumber)) {
                throwError("手机号不能为空", SsAuthServerConstant.PHONE_NUMBER);
            }
            if (!StringUtils.hasText(phoneCode)) {
                throwError("验证码不能为空", SsAuthServerConstant.PHONE_CODE);
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
