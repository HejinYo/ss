package cn.hejinyo.ss.common.framework.exception;

import cn.hejinyo.ss.common.consts.CommonConstant;
import cn.hejinyo.ss.common.framework.utils.ResponseUtils;
import cn.hejinyo.ss.common.framework.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
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
     * infoException，返回消息
     */
    @ExceptionHandler(InfoException.class)
    public void infoExceptionException(InfoException e, HttpServletRequest request, HttpServletResponse response) {
        this.handle(request, response, HttpStatus.OK, Result.error(e.getCode(), e.getMessage()));
    }

    /**
     * 实体验证
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void validException(MethodArgumentNotValidException mnve, HttpServletRequest request, HttpServletResponse response) {
        this.handle(request, response, HttpStatus.UNAUTHORIZED,
                Result.error(
                        Optional.ofNullable(mnve.getBindingResult().getFieldError())
                                .map(DefaultMessageSourceResolvable::getDefaultMessage
                                ).orElse("实体未通过校验")));
    }

    /**
     * 500 Internal Server Error
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {Exception.class})
    public void exception(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Result result;
        if (ex instanceof HttpMessageNotReadableException) {
            result = Result.error(HttpStatus.BAD_REQUEST.getReasonPhrase());
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            result = Result.error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
        } else if (ex instanceof HttpMediaTypeNotSupportedException) {
            result = Result.error(HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase());
        } else {
            log.error("系统发生未知错误异常", ex);
            result = Result.error("未知错误:" + ex.getMessage());
        }
        this.handle(request, response, HttpStatus.UNAUTHORIZED, result);
    }

    private void handle(HttpServletRequest request, HttpServletResponse response, HttpStatus httpStatus, Result result) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if ((contextPath + uri).startsWith(CommonConstant.MICRO_SERVER_API)) {
            // 如果是微服务调用，状态码为500
            ResponseUtils.response(response, HttpStatus.INTERNAL_SERVER_ERROR.value(), result);
        } else {
            ResponseUtils.response(response, httpStatus.value(), result);
        }
    }
}
