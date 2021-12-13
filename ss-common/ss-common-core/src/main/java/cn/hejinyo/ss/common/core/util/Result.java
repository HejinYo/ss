package cn.hejinyo.ss.common.core.util;

import cn.hejinyo.ss.common.core.constant.BaseStatusConstant;
import cn.hejinyo.ss.common.core.constant.CommonStatusConstant;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 返回结果
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/5 18:25
 */
@Getter
@Setter
public class Result<T> implements Serializable {
    private static final int SUCCESS = CommonStatusConstant.SUCCESS.getCode();
    private static final int ERROR = CommonStatusConstant.FAILURE.getCode();

    private String msg;
    private Integer code;
    private Object data;

    private Result() {
        super();
    }

    public static <T> Result<T> ok() {
        Result<T> result = new Result<>();
        result.setCode(SUCCESS);
        return result;
    }

    public static <T> Result<T> ok(Object data) {
        Result<T> result = ok();
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error() {
        Result<T> result = new Result<>();
        result.setCode(ERROR);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = error();
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> status(BaseStatusConstant statusCode) {
        Result<T> result = new Result<>();
        result.setCode(statusCode.getCode());
        result.setMsg(statusCode.getMsg());
        return result;
    }

}
