package cn.hejinyo.ss.common.exception;

/**
 * 自定义，返回消息的异常
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/5 18:46
 */
public class CommonException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_FAILURE_CODE = 0;
    private static final String DEFAULT_FAILURE_MESSAGE = "失败";

    protected int code = DEFAULT_FAILURE_CODE;


    public CommonException(int code) {
        super(DEFAULT_FAILURE_MESSAGE);
        this.code = code;
    }

    public CommonException(String message) {
        super(message);
    }

    public CommonException(Integer code, String message) {
        super(message);
        if (code != null) {
            this.code = code;
        }
    }

    public CommonException(Throwable cause) {
        super(cause);
    }

    public CommonException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
