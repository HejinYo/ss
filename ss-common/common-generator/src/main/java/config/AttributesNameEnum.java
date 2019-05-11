package config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/5/11 17:07
 */
@AllArgsConstructor
@Getter
public enum AttributesNameEnum {
    /**
     * BaseResultMap
     */
    ID("id", "id"),
    TYPE("type", "类型"),
    PARAMETER_TYPE("parameterType", "参数类型"),
    RESULT_MAP("resultMap", "結果集映射"),
    ;

    private String value;
    private String state;
}
