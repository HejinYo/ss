package cn.hejinyo.ss.auth.server.service.impl;

import cn.hejinyo.ss.auth.server.dto.AuthCheckResult;
import cn.hejinyo.ss.auth.server.feign.JellySysUserService;
import cn.hejinyo.ss.auth.server.service.JellyService;
import cn.hejinyo.ss.common.framework.consts.StatusCode;
import cn.hejinyo.ss.common.framework.exception.InfoException;
import cn.hejinyo.ss.common.framework.utils.JwtTools;
import cn.hejinyo.ss.common.redis.utils.RedisUtils;
import cn.hejinyo.ss.common.utils.Tools;
import cn.hejinyo.ss.jelly.consts.Constant;
import cn.hejinyo.ss.jelly.dto.SysUserDTO;
import cn.hejinyo.ss.jelly.dto.UserNameLoginDTO;
import cn.hejinyo.ss.jelly.tools.JellyRedisKeys;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/9/2 18:55
 */
@Service
@Slf4j
public class JellyServiceImpl implements JellyService {

    @Autowired
    private JellySysUserService jellySysUserService;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 验证登录用户
     */
    @Override
    public SysUserDTO checkUser(UserNameLoginDTO nameLoginVO) {
        // 根据用户名查找用户，进行密码匹配
        SysUserDTO userDTO = jellySysUserService.findByUserName(nameLoginVO.getUserName());
        // 如果无相关用户或已删除则返回null
        if (null == userDTO) {
            throw new InfoException(StatusCode.LOGIN_USER_NOEXIST);
        } else if (Constant.Status.DISABLE.equals(userDTO.getState())) {
            throw new InfoException(StatusCode.LOGIN_USER_LOCK);
        }
        log.debug("userDTO=====>{}",userDTO);
        //验证密码
        if (!Tools.checkPassword(nameLoginVO.getUserPwd(), userDTO.getUserSalt(), userDTO.getUserPwd())) {
            throw new InfoException(StatusCode.LOGIN_PASSWORD_ERROR);
        }
        return userDTO;
    }


    /**
     * 处理登录逻辑,创建token
     */
    @Override
    public String doLogin(SysUserDTO userDTO) {
        Integer userId = userDTO.getUserId();
        //创建jwt token
        String token = JwtTools.createToken("jelly", true, userId, userDTO.getUserName(), JwtTools.JWT_SIGN_KEY, Constant.JWT_EXPIRES_DEFAULT);
        String jti = JwtTools.tokenInfo(token, JwtTools.JWT_ID, String.class);
        redisUtils.hset(JellyRedisKeys.storeUser(userId), JellyRedisKeys.USER_TOKEN, jti);
        return token;
    }

    /**
     * 查找用户编号对应的角色编码字符串
     */
    @Override
    public Set<String> getUserRoleSet(int userId) {
        // redis中获得角色信息
        Set<String> roleSet = redisUtils.hget(JellyRedisKeys.storeUser(userId), JellyRedisKeys.USER_ROLE, new TypeToken<Set<String>>() {
        }.getType());

        // 不存在则数据库获得角色信息
        if (roleSet == null) {
            roleSet = jellySysUserService.getUserRoleSet(userId);
        }
        if (roleSet != null) {
            roleSet.removeIf(Objects::isNull);
            redisUtils.hset(JellyRedisKeys.storeUser(userId), JellyRedisKeys.USER_ROLE, roleSet);
        }
        return roleSet;
    }

    /**
     * 查找用户编号对应的权限编码字符串
     */
    @Override
    public Set<String> getUserPermSet(int userId) {
        // redis中获得权限信息
        Set<String> permissionsSet = redisUtils.hget(JellyRedisKeys.storeUser(userId), JellyRedisKeys.USER_PERM, new TypeToken<Set<String>>() {
        }.getType());

        // 不存在则数据库获得权限信息
        if (permissionsSet == null) {
            permissionsSet = jellySysUserService.getUserPermSet(userId);
        }
        if (permissionsSet != null) {
            permissionsSet.removeIf(Objects::isNull);
            redisUtils.hset(JellyRedisKeys.storeUser(userId), JellyRedisKeys.USER_PERM, permissionsSet);
        }
        return permissionsSet;
    }

    /**
     * 验证token 通过返回角色权限信息
     */
    @Override
    public AuthCheckResult checkToken(Integer userId, String jti) {
        String checkJti = redisUtils.hget(JellyRedisKeys.storeUser(userId), JellyRedisKeys.USER_TOKEN, String.class);
        AuthCheckResult result = new AuthCheckResult();
        // 需要优化，减少请求redis次数
        if (jti.equals(checkJti)) {
            result.setPass(true);
            result.setRoleSet(getUserRoleSet(userId));
            result.setPermSet(getUserPermSet(userId));
        }
        return result;
    }

    /**
     * 注销
     */
    @Override
    public void logout(String token) {
        try {
            //验证token是否有效
            JwtTools.verifyToken(token, JwtTools.JWT_SIGN_KEY);
            //token中获取用户名
            Integer userId = JwtTools.tokenInfo(token, JwtTools.JWT_TOKEN_USERID, Integer.class);
            String jti = JwtTools.tokenInfo(token, JwtTools.JWT_ID, String.class);
            //查询缓存中的用户信息
            String checkJti = redisUtils.hget(JellyRedisKeys.storeUser(userId), JellyRedisKeys.USER_TOKEN, String.class);
            if (jti.equals(checkJti)) {
                //清除用户原来所有缓存
                redisUtils.delete(JellyRedisKeys.storeUser(userId));
            }
        } catch (Exception ignored) {
        }
    }
}
