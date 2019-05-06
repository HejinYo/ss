package cn.hejinyo.ss.auth.jelly.exception;

import cn.hejinyo.ss.common.framework.consts.StatusCode;
import cn.hejinyo.ss.common.framework.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

/**
 * @author : HejinYo   hejinyo@gmail.com 2017/8/13 19:12
 * @apiNote :
 */
@RestControllerAdvice
@Slf4j
public class AuthExceptionHandle {

    /**
     * 401 UNAUTHORIZED，无权限
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({UnauthorizedException.class})
    public Result shiroException(UnauthorizedException ex, HttpServletResponse response) {
        return Result.error(StatusCode.USER_UNAUTHORIZED);
    }

}
