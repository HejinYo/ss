package cn.hejinyo.ss.common.core.exception;

import cn.hejinyo.ss.common.core.constant.BaseStatusConstant;
import cn.hejinyo.ss.common.core.constant.CommonStatusConstant;

/**
 * 信息提示异常类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/3/27 17:50
 */
public class InfoException extends RuntimeException {

    private final int code;

    private static final int DEFAULT_FAILURE_CODE = CommonStatusConstant.FAILURE.getCode();

    private static final String DEFAULT_FAILURE_MESSAGE = CommonStatusConstant.FAILURE.getMsg();

    public InfoException(BaseStatusConstant statusCode) {
        super(statusCode.getMsg());
        this.code = statusCode.getCode();
    }

    public InfoException(int code) {
        super(DEFAULT_FAILURE_MESSAGE);
        this.code = code;
    }

    public InfoException(int code, String message) {
        super(message);
        this.code = code;
    }

    public InfoException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public InfoException(String message) {
        super(message);
        this.code = DEFAULT_FAILURE_CODE;
    }

    public InfoException(Throwable cause) {
        super(cause);
        this.code = DEFAULT_FAILURE_CODE;
    }

    public InfoException(String message, Throwable cause) {
        super(message, cause);
        this.code = DEFAULT_FAILURE_CODE;
    }

    int getCode() {
        return code;
    }
}
