package cn.hejinyo.ss.jelly.api;

import cn.hejinyo.ss.common.utils.PojoConvertUtil;
import cn.hejinyo.ss.jelly.dao.SysUserDao;
import cn.hejinyo.ss.jelly.dto.SysUserDTO;
import cn.hejinyo.ss.jelly.entity.SysUserEntity;
import cn.hejinyo.ss.jelly.service.SysUserApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * 微服务api接口
 *
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/04/27 00:58
 */
@RestController
public class SysUserApi implements SysUserApiService {

    @Autowired
    private SysUserDao sysUserDao;

    /**
     * 获取用户信息
     */
    @Override
    public SysUserDTO getUserInfo(@PathVariable Integer userId) {
        SysUserEntity userEntity = sysUserDao.getOne(userId);
        return Optional.of(userEntity).map(v -> PojoConvertUtil.convert(v, SysUserDTO.class)).orElse(null);
    }

    /**
     * 获取用户信息
     */
    @Override
    public SysUserDTO findByUserName(@PathVariable String userName) {
        return this.getUserInfo(1);
    }

    /**
     * 获得角色信息
     */
    @Override
    public Set<String> getUserRoleSet(@PathVariable int userId) {
        return new HashSet<>(Arrays.asList("admin", "class"));
    }

    /**
     * 获得权限信息
     */
    @Override
    public Set<String> getUserPermSet(@PathVariable int userId) {
        return new HashSet<>(Arrays.asList("select", "update"));
    }
}
