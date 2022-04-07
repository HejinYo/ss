package cn.hejinyo.ss.admin.repository;

import cn.hejinyo.ss.admin.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * 用户查询
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/4/6 22:28
 */
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    /**
     * 通过用户名称查询
     *
     * @param username String
     * @return UserEntity
     */
    UserEntity findByUsername(String username);
}
