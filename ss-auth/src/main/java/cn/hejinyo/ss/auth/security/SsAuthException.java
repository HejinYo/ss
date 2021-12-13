package cn.hejinyo.ss.auth.security;

import org.springframework.security.core.AuthenticationException;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/13 19:56
 */
public class SsAuthException extends AuthenticationException {

    /**
     * Constructs an {@code AuthenticationException} with the specified message and no
     * root cause.
     *
     * @param msg the detail message
     */
    public SsAuthException(String msg) {
        super(msg);
    }
}
