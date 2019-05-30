package cn.hejinyo.ss.jelly.service.impl;

import cn.hejinyo.ss.common.utils.PojoConvertUtil;
import cn.hejinyo.ss.jelly.dao.SysUserDao;
import cn.hejinyo.ss.jelly.model.dto.SysUserDTO;
import cn.hejinyo.ss.jelly.model.entity.SysUserEntity;
import cn.hejinyo.ss.jelly.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/5/30 22:04
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserDao sysUserDao;

    /**
     * 根据用户名查询用户信息
     *
     * @param userName 用户名
     */
    @Override
    public SysUserDTO getByUserName(String userName) {
        SysUserEntity userEntity = sysUserDao.findByUserName(userName);
        return Optional.ofNullable(userEntity).map(v -> PojoConvertUtil.convert(v, SysUserDTO.class)).orElse(null);
    }
}
