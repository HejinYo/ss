package cn.hejinyo.ss.auth.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登陆成功处理器
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/11/3 21:56
 */

public class SsAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final String url;

    private final boolean isRedirect;

    public SsAuthenticationSuccessHandler(String url, boolean isRedirect) {
        this.url = url;
        this.isRedirect = isRedirect;
    }

    /**
     * Called when a user has been successfully authenticated.
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication 用户认证成功后包含用户名和权限集合，内容为自定义UserDetailService实现的方法提供
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (isRedirect) {
            // 重定向
            response.sendRedirect(url);
        } else {
            // 转发
            request.getRequestDispatcher(url).forward(request, response);
        }
    }
}
