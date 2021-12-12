package cn.hejinyo.ss.auth.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务状态码
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/5 18:25
 */
@AllArgsConstructor
@Getter
public enum CommonStatusConstant implements BaseStatusConstant {
    /**
     * 通用业务状态码
     */
    FAILURE(-1, "失败"),
    SUCCESS(0, "成功");

    private final int code;
    private final String msg;
}
