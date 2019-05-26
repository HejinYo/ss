package cn.hejinyo.ss.common.framework.utils;

import cn.hejinyo.ss.common.framework.consts.StatusCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/19 17:31
 * 返回JSON结果 工具类
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {
    private static final int SUCCESS = 1;
    private static final int ERROR = 0;

    private String msg;
    private int code;
    private T result;

    public static <T> Result<T> ok() {
        Result<T> r = new Result<>();
        r.setCode(SUCCESS);
        return r;
    }

    public static <T> Result<T> ok(String message) {
        Result<T> r = ok();
        r.setMsg(message);
        return r;
    }

    public static <T> Result<T> result(T result) {
        Result<T> r = ok();
        r.setResult(result);
        return r;
    }

    public static <T> Result<T> ok(String message, T result) {
        Result<T> r = ok();
        r.setMsg(message);
        r.setResult(result);
        return r;
    }


    public static <T> Result<T> error(String message) {
        Result<T> r = new Result<>();
        r.setCode(ERROR);
        r.setMsg(message);
        return r;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(message);
        return r;
    }

    public static <T> Result<T> error(StatusCode statusCode) {
        Result<T> r = new Result<>();
        r.setCode(statusCode.getCode());
        r.setMsg(statusCode.getMsg());
        return r;
    }

}
