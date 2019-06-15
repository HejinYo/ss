package cn.hejinyo.ss.jelly.handler;


import cn.hejinyo.ss.common.utils.JsonUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * mybatis json 字符串转对象映射
 *
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/04/09 16:52
 */
/*@MappedTypes({JSONObject.class})*/
@MappedJdbcTypes({JdbcType.VARCHAR, JdbcType.CHAR, JdbcType.LONGVARCHAR, JdbcType.CLOB})
public class JsonTypeHandler<T> extends BaseTypeHandler<T> {

    private Class<T> clazz;

    public JsonTypeHandler(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.clazz = clazz;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JsonUtil.toJSONString(parameter));
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return Optional.ofNullable(rs.getString(columnName)).map(v -> JsonUtil.parseObject(v, clazz)).orElse(null);

    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return Optional.ofNullable(rs.getString(columnIndex)).map(v -> JsonUtil.parseObject(v, clazz)).orElse(null);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return Optional.ofNullable(cs.getString(columnIndex)).map(v -> JsonUtil.parseObject(v, clazz)).orElse(null);
    }

}
