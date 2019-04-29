package cn.hejinyo.ss.jelly.api;

import cn.hejinyo.ss.jelly.dao.SysUserDao;
import cn.hejinyo.ss.jelly.dto.SysUserDTO;
import cn.hejinyo.ss.jelly.entity.SysUserEntity;
import cn.hejinyo.ss.jelly.service.SysUserApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

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
        return Optional.of(userEntity).map(v -> {
            SysUserDTO userDTO = new SysUserDTO();
            userDTO.setUserId(v.getUserId());
            userDTO.setUserName(v.getUserName());
            return userDTO;
        }).orElse(null);

    }
}
