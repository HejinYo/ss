package config;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl;

import java.sql.Types;

/**
 * 自定义处理数据库字段和Java实体类型映射
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/5/10 23:06
 */
public class MyJavaTypeResolver extends JavaTypeResolverDefaultImpl {

    public MyJavaTypeResolver() {
        super();
        // tinyint 对应 Integer
        typeMap.put(Types.TINYINT, new JdbcTypeInformation("TINYINT", new FullyQualifiedJavaType(Integer.class.getName())));
    }
}
