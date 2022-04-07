package cn.hejinyo.ss.admin.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/3/28 20:32
 */
@Getter
@AllArgsConstructor
public enum UserStateEnum {

    /**
     * 禁用
     */
    DISABLE(0, "禁用"),

    /**
     * 启用
     */
    ENABLED(1, "启用"),

    /**
     * 锁定
     */
    LOCK(2, "锁定");

    @JsonValue
    private Integer value;
    private String desc;
}
