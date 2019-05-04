package cn.hejinyo.ss.common.framework.exception;

import cn.hejinyo.ss.common.consts.BaseStatusCode;
import cn.hejinyo.ss.common.exception.CommonException;

/**
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/05/04 18:56
 */
public class InfoException extends CommonException {

    public InfoException(BaseStatusCode statusCode) {
        super(statusCode.getMsg());
        super.code = statusCode.getCode();
    }

    public InfoException(int code) {
        super(code);
    }

    public InfoException(String message) {
        super(message);
    }

    public InfoException(int code, String message) {
        super(code, message);
    }

    public InfoException(Throwable cause) {
        super(cause);
    }

    public InfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public InfoException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
