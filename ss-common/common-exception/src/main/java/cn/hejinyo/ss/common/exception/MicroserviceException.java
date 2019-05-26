package cn.hejinyo.ss.common.exception;

/**
 * 微服务异常
 *
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/05/04 18:56
 */
public class MicroserviceException extends CommonException {

    public MicroserviceException(int code) {
        super(code);
    }

    public MicroserviceException(String message) {
        super(message);
    }

    public MicroserviceException(int code, String message) {
        super(code, message);
    }

    public MicroserviceException(Throwable cause) {
        super(cause);
    }

    public MicroserviceException(String message, Throwable cause) {
        super(message, cause);
    }

    public MicroserviceException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
