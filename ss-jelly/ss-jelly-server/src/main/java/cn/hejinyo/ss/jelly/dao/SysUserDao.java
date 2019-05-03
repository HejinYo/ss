package cn.hejinyo.ss.jelly.dao;

import cn.hejinyo.ss.jelly.entity.SysUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/19 23:00
 */
@Repository
public interface SysUserDao extends JpaRepository<SysUserEntity, Integer> {
    SysUserEntity findByUserName(String username);
}
