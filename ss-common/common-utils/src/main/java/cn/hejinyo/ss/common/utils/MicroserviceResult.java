package cn.hejinyo.ss.common.utils;

import cn.hejinyo.ss.common.exception.CommonException;
import cn.hejinyo.ss.common.exception.MicroserviceException;
import lombok.Data;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 返回JSON结果 工具类
 */
@Data
public class MicroserviceResult<T> {
    private static final int SUCCESS = 1;
    private static final int ERROR = 0;
    private static final int STATUS = 200;
    private static final int ERROR_STATUS = 500;

    private String msg;

    private int code;

    private T result;

    /**
     * http 状态码封装，默认200
     */
    private int status = STATUS;


    private MicroserviceResult() {

    }

    public static MicroserviceResult ok() {
        MicroserviceResult r = new MicroserviceResult();
        r.setCode(SUCCESS);
        return r;
    }

    public static MicroserviceResult ok(String message) {
        MicroserviceResult r = ok();
        r.setMsg(message);
        return r;
    }

    public static <T> MicroserviceResult<T> ok(T result) {
        MicroserviceResult<T> r = new MicroserviceResult<>();
        r.setCode(SUCCESS);
        r.setResult(result);
        return r;
    }


    public static <T> MicroserviceResult<T> ok(String message, T result) {
        MicroserviceResult<T> r = new MicroserviceResult<>();
        r.setMsg(message);
        r.setResult(result);
        return r;
    }

    public static <T> MicroserviceResult<T> fallback(String message, Object... param) {
        MicroserviceResult<T> r = new MicroserviceResult<>();
        r.setStatus(ERROR_STATUS);
        r.setCode(ERROR);
        String msg = message;
        for (Object o : param) {
            msg = msg.replaceFirst("\\{}", o.toString());
        }
        r.setMsg(msg);
        return r;
    }

    public static <T> MicroserviceResult<T> result(int status, int code, String message, T result) {
        MicroserviceResult<T> r = new MicroserviceResult<>();
        r.setStatus(status);
        r.setCode(code);
        r.setMsg(message);
        r.setResult(result);
        return r;
    }

    public <R> R then(Function<? super T, ? extends R> mapper) {
        if (this.code == SUCCESS) {
            return mapper.apply(this.result);
        }
        if (this.status == STATUS) {
            throw new CommonException(this.code, this.msg);
        }
        throw new MicroserviceException(this.msg);
    }

    public T get() {
        if (this.code == SUCCESS) {
            return this.result;
        }
        if (this.status == STATUS) {
            throw new CommonException(this.code, this.msg);
        }
        throw new MicroserviceException(this.msg);
    }


}
