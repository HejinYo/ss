package cn.hejinyo.ss.auth.jelly.filter;

import cn.hejinyo.ss.auth.jelly.feign.JellyAuthService;
import cn.hejinyo.ss.auth.jelly.token.SsAuthToken;
import cn.hejinyo.ss.auth.server.dto.AuthCheckResult;
import cn.hejinyo.ss.common.consts.CommonConstant;
import cn.hejinyo.ss.common.framework.consts.StatusCode;
import cn.hejinyo.ss.common.framework.utils.JwtTools;
import cn.hejinyo.ss.common.framework.utils.WebUtils;
import cn.hejinyo.ss.common.framework.utils.Result;
import cn.hejinyo.ss.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.web.filter.AccessControlFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/7/29 18:05
 */
@Slf4j
public class SsAuthcFilter extends AccessControlFilter {

    /**
     * bean的方式依赖注入需要用构造器注入
     */
    private JellyAuthService jellyAuthService;

    public SsAuthcFilter(JellyAuthService jellyAuthService) {
        this.jellyAuthService = jellyAuthService;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String userToken = WebUtils.getRequestToken(httpRequest);
        log.info("auth拦截 userToken=====>{}", StringUtils.isNotEmpty(userToken));
        try {
            if (StringUtils.isNotEmpty(userToken)) {
                // 验证token有效性
                JwtTools.verifyToken(userToken, JwtTools.JWT_SIGN_KEY);
                String sub = JwtTools.tokenInfo(userToken, JwtTools.JWT_SUB, String.class);
                // 只允许jelly登录用户访问
                if (CommonConstant.JELLY_AUTH.equals(sub)) {
                    Integer userId = JwtTools.tokenInfo(userToken, JwtTools.JWT_TOKEN_USERID, Integer.class);
                    String jti = JwtTools.tokenInfo(userToken, JwtTools.JWT_ID, String.class);
                    AuthCheckResult result = jellyAuthService.checkToken(userId, jti).get();
                    log.info("AuthCheckResult=====>{}", result);
                    if (result != null && result.isPass()) {
                        String userName = JwtTools.tokenInfo(userToken, JwtTools.JWT_TOKEN_USERNAME, String.class);
                        getSubject(request, response).login(new SsAuthToken(userName, userId, userToken, result.getRoleSet(), result.getPermSet()));
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            log.debug("userToken 验证失败 ：{}", userToken, e);
        }
        WebUtils.response(response, Result.error(StatusCode.TOKEN_FAULT));
        return false;
    }
}

