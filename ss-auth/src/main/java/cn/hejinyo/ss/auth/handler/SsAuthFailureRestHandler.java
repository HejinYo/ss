package cn.hejinyo.ss.auth.handler;

import cn.hejinyo.ss.auth.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登陆失败处理器
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/11/3 22:27
 */
@Slf4j
public class SsAuthFailureRestHandler implements AuthenticationFailureHandler {

    private final String failureType;

    public SsAuthFailureRestHandler(String msg) {
        this.failureType = msg;
    }

    /**
     * Called when an authentication attempt fails.
     *
     * @param request   the request during which the authentication attempt occurred.
     * @param response  the response.
     * @param exception the exception which was thrown to reject the authentication
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        OAuth2AuthenticationException authException = (OAuth2AuthenticationException) exception;
        OAuth2Error error = authException.getError();
        log.error("{} => {}", failureType, error.getDescription(), exception);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        HttpUtils.writeResult(response, failureType + " => " + error);
    }
}
