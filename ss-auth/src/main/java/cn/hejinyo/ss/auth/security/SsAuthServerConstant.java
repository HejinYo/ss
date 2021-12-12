package cn.hejinyo.ss.auth.security;

import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

/**
 * SsAuthServer 常量
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/11/3 22:27
 */
public class SsAuthServerConstant implements OAuth2ParameterNames {

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
