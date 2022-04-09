package cn.hejinyo.ss.common.core.enums;

import cn.hejinyo.ss.common.core.exception.InfoException;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 通用枚举转换
 *
 * @param <E> 枚举类型
 * @param <T> 枚举值类型
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/4/9 17:42
 */
@Converter
public abstract class BaseEnumConverter<E extends BaseEnum<T>, T> implements AttributeConverter<BaseEnum<T>, T> {

    private final Map<T, E> cacheMap = new LinkedHashMap<>();

    /**
     * 枚举构造方法，反射获取泛xing
     */
    @SuppressWarnings("unchecked")
    protected BaseEnumConverter() {
        try {
            Class<E> clazz = (Class<E>) (((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments())[0];
            E[] values = (E[]) clazz.getMethod("values").invoke(null);
            for (E x : values) {
                cacheMap.put(x.getValue(), x);
            }
        } catch (Exception e) {
            throw new InfoException("枚举转换初始化失败：" + e.getMessage());
        }
    }

    /**
     * 枚举构造方法
     *
     * @param enumType 枚举类型
     */
    protected BaseEnumConverter(Class<E> enumType) {
        for (E enumConstant : enumType.getEnumConstants()) {
            cacheMap.put(enumConstant.getValue(), enumConstant);
        }
    }

    @Override
    public T convertToDatabaseColumn(BaseEnum<T> attribute) {
        return attribute.getValue();
    }

    @Override
    public E convertToEntityAttribute(T dbData) throws RuntimeException {
        return cacheMap.get(dbData);
    }
}
