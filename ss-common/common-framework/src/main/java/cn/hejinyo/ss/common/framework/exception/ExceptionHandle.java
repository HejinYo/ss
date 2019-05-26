package cn.hejinyo.ss.common.framework.exception;

import cn.hejinyo.ss.common.consts.CommonConstant;
import cn.hejinyo.ss.common.exception.CommonException;
import cn.hejinyo.ss.common.exception.MicroserviceException;
import cn.hejinyo.ss.common.framework.utils.ResponseUtils;
import cn.hejinyo.ss.common.framework.utils.Result;
import cn.hejinyo.ss.common.utils.MicroserviceResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * @author : HejinYo   hejinyo@gmail.com 2017/8/13 19:12
 * @apiNote :
 */
@RestControllerAdvice
@Slf4j
public class ExceptionHandle {


    /**
     * CommonException，返回消息
     */
    @ExceptionHandler(CommonException.class)
    public void infoExceptionException(CommonException e, HttpServletRequest request, HttpServletResponse response) {
        this.handle(request, response, HttpStatus.OK, Result.error(e.getCode(), e.getMessage()));
    }

    /**
     * infoException，返回消息
     */
    @ExceptionHandler(InfoException.class)
    public void infoExceptionException(InfoException e, HttpServletRequest request, HttpServletResponse response) {
        this.handle(request, response, HttpStatus.OK, Result.error(e.getCode(), e.getMessage()));
    }

    /**
     * MicroserviceException，微服务异常返回消息
     */
    @ExceptionHandler(MicroserviceException.class)
    public void infoExceptionException(MicroserviceException e, HttpServletRequest request, HttpServletResponse response) {
        log.error("微服务访问异常", e);
        Result r = Result.error(e.getCode(), "微服务访问异常：" + e.getMessage());
        this.handle(request, response, HttpStatus.INTERNAL_SERVER_ERROR, r);
    }

    /**
     * 实体验证
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void validException(MethodArgumentNotValidException mnve, HttpServletRequest request, HttpServletResponse response) {
        Result r = Result.error(Optional.ofNullable(mnve.getBindingResult().getFieldError())
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("实体未通过校验"));
        this.handle(request, response, HttpStatus.OK, r);
    }

    /**
     * 500 Internal Server Error
     */
    @ExceptionHandler(value = {Exception.class})
    public void exception(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        log.error("系统发生未知错误异常", ex);
        this.handle(request, response, HttpStatus.INTERNAL_SERVER_ERROR, Result.error("服务器错误:" + ex.getMessage()));
    }


    /**
     * 如果访问的是内部微服务接口，则封装成微服务响应对象
     */
    private void handle(HttpServletRequest request, HttpServletResponse response, HttpStatus httpStatus, Result result) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if ((contextPath + uri).startsWith(CommonConstant.MICRO_SERVER_API)) {
            // 如果是微服务调用，状态码为200,返回 ServerResult对象
            MicroserviceResult re = MicroserviceResult.result(httpStatus.value(), result.getCode(), result.getMsg(), result.getResult());
            ResponseUtils.response(response, HttpStatus.OK.value(), re);
        } else {
            ResponseUtils.response(response, httpStatus.value(), result);
        }
    }
}
