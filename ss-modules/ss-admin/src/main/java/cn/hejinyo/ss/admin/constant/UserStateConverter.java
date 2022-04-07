package cn.hejinyo.ss.admin.constant;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

/**
 * 用户状态枚举JPA转换
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/4/7 22:18
 */
@Converter(autoApply = true)
public class UserStateConverter implements AttributeConverter<UserStateEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserStateEnum userStateEnum) {
        if (userStateEnum == null) {
            return null;
        }
        return userStateEnum.getValue();
    }

    @Override
    public UserStateEnum convertToEntityAttribute(Integer value) {
        if (value == null) {
            return null;
        }
        return Stream.of(UserStateEnum.values())
                .filter(c -> c.getValue().equals(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
