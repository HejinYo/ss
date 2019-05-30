package cn.hejinyo.ss.jelly.dao;

import cn.hejinyo.ss.common.framework.base.BaseDao;
import cn.hejinyo.ss.jelly.model.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * sys_user 持久化层
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2019/05/30 21:53
 */
@Mapper
public interface SysUserDao extends BaseDao<SysUserEntity, Integer> {

    /**
     * 根据用户名查询用户信息
     *
     * @param userName 用户名
     * @return 用户信息
     */
    SysUserEntity findByUserName(String userName);
}