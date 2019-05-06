package cn.hejinyo.ss.common.framework.exception;

import cn.hejinyo.ss.common.consts.CommonConstant;
import cn.hejinyo.ss.common.framework.utils.ResponseUtils;
import cn.hejinyo.ss.common.framework.utils.Result;
import lombok.extern.slf4j.Slf4j;
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
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if ((contextPath + uri).startsWith(CommonConstant.MICRO_SERVER_API)) {
            // 如果是微服务调用，状态码为500
            ResponseUtils.response(response, HttpStatus.INTERNAL_SERVER_ERROR.value(), Result.error(e.getCode(), e.getMessage()));
        } else {
            ResponseUtils.response(response, HttpStatus.OK.value(), Result.error(e.getCode(), e.getMessage()));
        }
    }

    /**
     * 实体验证
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result validException(MethodArgumentNotValidException mnve) {
        return Result.error(mnve.getBindingResult().getFieldError().getDefaultMessage());
    }

    /**
     * 500 Internal Server Error
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {Exception.class})
    public Result exception(Exception ex, HttpServletResponse response) {
        if (ex instanceof HttpMessageNotReadableException) {
            return Result.error(HttpStatus.BAD_REQUEST.getReasonPhrase());
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            return Result.error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
        } else if (ex instanceof HttpMediaTypeNotSupportedException) {
            return Result.error(HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase());
        } else {
            ex.printStackTrace();
        }
        log.error("系统发生未知错误异常", ex);
        return Result.error("未知错误:" + ex.getMessage());
    }
}
