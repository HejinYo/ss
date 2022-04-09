package cn.hejinyo.ss.common.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据状态 0=删除 1=正常
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/4/9 21:45
 */
@Getter
@AllArgsConstructor
public enum DataStateEnum implements BaseEnum<Integer> {

    /**
     * 删除
     */
    DELETED(0, "删除"),

    /**
     * 正常
     */
    NORMAL(1, "正常");

    @JsonValue
    private Integer value;
    private String desc;

    @javax.persistence.Converter(autoApply = true)
    public static class Converter extends BaseEnumConverter<DataStateEnum, Integer> {

    }
}
