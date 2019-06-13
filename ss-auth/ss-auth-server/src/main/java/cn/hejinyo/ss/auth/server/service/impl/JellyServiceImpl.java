package cn.hejinyo.ss.auth.server.service.impl;

import cn.hejinyo.ss.auth.server.dto.AuthCheckResult;
import cn.hejinyo.ss.auth.server.feign.JellySysUserService;
import cn.hejinyo.ss.auth.server.service.JellyService;
import cn.hejinyo.ss.common.consts.CommonConstant;
import cn.hejinyo.ss.common.framework.consts.StatusCode;
import cn.hejinyo.ss.common.framework.exception.InfoException;
import cn.hejinyo.ss.common.framework.utils.JwtTools;
import cn.hejinyo.ss.common.redis.utils.RedisUtils;
import cn.hejinyo.ss.common.utils.JsonUtil;
import cn.hejinyo.ss.common.utils.Tools;
import cn.hejinyo.ss.jelly.model.dto.SysUserDTO;
import cn.hejinyo.ss.jelly.model.dto.UserNameLoginDTO;
import cn.hejinyo.ss.jelly.tools.JellyRedisKeys;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
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

    /**
     * 验证登录用户
     */
    @Override
    public SysUserDTO checkUser(UserNameLoginDTO nameLoginVO) {
        // 根据用户名查找用户，进行密码匹配
        return jellySysUserService.findByUserName(nameLoginVO.getUserName()).then(userDTO -> {
            // 如果无相关用户或已删除则返回null
            if (null == userDTO) {
                throw new InfoException(StatusCode.LOGIN_USER_NOEXIST);
            } else if (CommonConstant.Status.DISABLE.equals(userDTO.getState())) {
                throw new InfoException(StatusCode.LOGIN_USER_LOCK);
            }
            //验证密码
            if (!Tools.checkPassword(nameLoginVO.getUserPwd(), userDTO.getUserSalt(), userDTO.getUserPwd())) {
                throw new InfoException(StatusCode.LOGIN_PASSWORD_ERROR);
            }
            return userDTO;
        });
    }


    /**
     * 处理登录逻辑,创建token
     */
    @Override
    public String doLogin(SysUserDTO userDTO) {
        Integer userId = userDTO.getUserId();
        //创建jwt token
        String token = JwtTools.createToken(CommonConstant.JELLY_AUTH, true,
                userId, userDTO.getUserName(), JwtTools.JWT_SIGN_KEY, CommonConstant.JWT_EXPIRES_DEFAULT);
        String jti = JwtTools.tokenInfo(token, JwtTools.JWT_ID, String.class);
        RedisUtils.hset(JellyRedisKeys.storeUser(userId), JellyRedisKeys.USER_TOKEN, jti);
        return token;
    }

    /**
     * 验证token 通过返回角色权限信息
     */
    @Override
    public AuthCheckResult checkToken(Integer userId, String jti) {
        List<String> fields = Arrays.asList(JellyRedisKeys.USER_TOKEN, JellyRedisKeys.USER_ROLE, JellyRedisKeys.USER_PERM);
        // 读取用户TOKEN 角色 权限信息
        List<String> storeUserInfo = RedisUtils.hmget(JellyRedisKeys.storeUser(userId), fields);
        AuthCheckResult result = new AuthCheckResult();
        if (storeUserInfo.size() == fields.size()) {
            String checkJti = storeUserInfo.get(0);
            String roleStr = storeUserInfo.get(1);
            String permStr = storeUserInfo.get(2);
            // 验证成功
            if (jti.equals(checkJti)) {
                result.setPass(true);
                result.setRoleSet(getUserRoleSet(userId, roleStr));
                result.setPermSet(getUserPermSet(userId, permStr));
            }
        }
        return result;
    }

    /**
     * 查找用户编号对应的角色编码字符串
     */
    private Set<String> getUserRoleSet(int userId, String roleStr) {
        Set<String> roleSet;
        // redis中获得角色信息
        if (roleStr != null) {
            roleSet = JsonUtil.parseObject(roleStr, new TypeReference<Set<String>>() {
            }.getType());
        } else {
            // 不存在则数据库获得角色信息
            roleSet = jellySysUserService.getUserRoleSet(userId).get();
        }
        if (roleSet != null) {
            roleSet.removeIf(Objects::isNull);
            RedisUtils.hset(JellyRedisKeys.storeUser(userId), JellyRedisKeys.USER_ROLE, roleSet);
        }
        return roleSet;
    }

    /**
     * 查找用户编号对应的权限编码字符串
     */
    private Set<String> getUserPermSet(int userId, String permStr) {
        Set<String> permissionsSet;

        // redis中获得权限信息
        if (permStr != null) {
            permissionsSet = JsonUtil.parseObject(permStr, new TypeReference<Set<String>>() {
            }.getType());
        } else {
            // 不存在则数据库获得权限信息
            permissionsSet = jellySysUserService.getUserPermSet(userId).get();
        }

        if (permissionsSet != null) {
            permissionsSet.removeIf(Objects::isNull);
            RedisUtils.hset(JellyRedisKeys.storeUser(userId), JellyRedisKeys.USER_PERM, permissionsSet);
        }
        return permissionsSet;
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
            String checkJti = RedisUtils.hget(JellyRedisKeys.storeUser(userId), JellyRedisKeys.USER_TOKEN, String.class);
            if (jti.equals(checkJti)) {
                //清除用户原来所有缓存
                RedisUtils.delete(JellyRedisKeys.storeUser(userId));
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 获得当前用户redis中的用户信息
     */
    @Override
    public SysUserDTO getUserInfo(String userToken) {
        try {
            //验证token是否有效
            JwtTools.verifyToken(userToken, JwtTools.JWT_SIGN_KEY);
            //token中获取用户名
            String userName = JwtTools.tokenInfo(userToken, JwtTools.JWT_TOKEN_USERNAME, String.class);
            return jellySysUserService.findByUserName(userName).get();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
