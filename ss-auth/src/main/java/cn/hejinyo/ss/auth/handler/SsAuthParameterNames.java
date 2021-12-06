package cn.hejinyo.ss.auth.handler;

import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

/**
 * ss 认证参数
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/6 09:45
 */
public class SsAuthParameterNames implements OAuth2ParameterNames {

    /**
     * {@code principal_type} -  username=用户名密码登陆 phone =手机验证码登陆
     */
    public final static String PRINCIPAL_TYPE = "principal_type";

    /**
     * {@code username} -  username=用户名密码登陆
     */
    public final static String PRINCIPAL_USERNAME = "principal_username";

    /**
     * {@code phone} -   phone =手机验证码登陆
     */
    public final static String PRINCIPAL_PHONE = "principal_phone";

    /**
     * {@code phone} - 登陆手机号
     */
    public final static String PHONE_NUMBER = "phone_number";

    /**
     * {@code verification_code} - 手机验证码
     */
    public final static String PHONE_CODE = "phone_code";
}
