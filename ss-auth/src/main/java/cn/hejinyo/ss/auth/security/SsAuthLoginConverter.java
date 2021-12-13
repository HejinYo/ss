package cn.hejinyo.ss.auth.security;

import org.springframework.security.core.Authentication;
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

        // 用户名密码登陆
        if (SsAuthServerConstant.PRINCIPAL_USERNAME.equals(principalType)) {
            String username = parameters.getFirst(SsAuthServerConstant.USERNAME);
            String password = parameters.getFirst(SsAuthServerConstant.PASSWORD);
            if (!StringUtils.hasText(username)) {
                throw new SsAuthException("用户名称不能为空");
            }
            if (!StringUtils.hasText(password)) {
                throw new SsAuthException("用户密码不能为空");
            }
            return new SsAuthLoginToken(clientId, principalType, username, password);
        }

        // 手机验证码
        if (SsAuthServerConstant.PRINCIPAL_PHONE.equals(principalType)) {
            String phoneNumber = parameters.getFirst(SsAuthServerConstant.PHONE_NUMBER);
            String phoneCode = parameters.getFirst(SsAuthServerConstant.PHONE_CODE);
            if (!StringUtils.hasText(phoneNumber)) {
                throw new SsAuthException("手机号不能为空");
            }
            if (!StringUtils.hasText(phoneCode)) {
                throw new SsAuthException("验证码不能为空");
            }
            return new SsAuthLoginToken(clientId, principalType, phoneNumber, phoneCode);
        }
        throw new SsAuthException("authentication failed: " + SsAuthServerConstant.PRINCIPAL_TYPE);
    }
}
