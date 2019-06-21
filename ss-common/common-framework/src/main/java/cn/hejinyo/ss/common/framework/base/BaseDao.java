package cn.hejinyo.ss.common.framework.base;

import cn.hejinyo.ss.common.utils.PageQuery;

import java.io.Serializable;
import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/26 18:28
 */
public interface BaseDao<T, ID extends Serializable> {

    /**
     * 增加
     */
    int save(T entity);

    /**
     * 增加所有字段
     */
    int saveFull(T entity);

    /**
     * 更新
     */
    int update(T entity);

    /**
     * 更新所有字段
     */
    int updateFull(T entity);

    /**
     * 删除主键记录
     */
    int deleteByPk(ID id);

    /**
     * 根据主键查找一条记录
     */
    T findByPk(ID id);

    /**
     * 查询列表
     */
    List<T> findList(T entity);

    /**
     * 查询分页
     */
    List<T> findPage(PageQuery pageQuery);


}
