package cn.hejinyo.ss.auth.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
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
public class SsAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final String url;

    private final boolean isRedirect;

    public SsAuthenticationFailureHandler(String url, boolean isRedirect) {
        this.url = url;
        this.isRedirect = isRedirect;
    }

    /**
     * Called when an authentication attempt fails.
     *
     * @param request   the request during which the authentication attempt occurred.
     * @param response  the response.
     * @param exception the exception which was thrown to reject the authentication
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.debug("登录失败:{}", exception.getLocalizedMessage());
        String toUrl = String.format("%s?error=%s", url, exception.getMessage());
        if (isRedirect) {
            // 重定向
            response.sendRedirect(toUrl);
        } else {
            // 转发
            request.getRequestDispatcher(toUrl).forward(request, response);
        }
    }
}
