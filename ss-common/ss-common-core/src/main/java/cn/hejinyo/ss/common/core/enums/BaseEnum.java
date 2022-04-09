package cn.hejinyo.ss.common.core.enums;

/**
 * 基础枚举接口
 *
 * @param <T> T
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/4/9 17:38
 */
public interface BaseEnum<T> {

    /**
     * 枚举值
     *
     * @return T
     */
    T getValue();

    /**
     * 枚举说明
     *
     * @return String
     */
    String getDesc();

}
