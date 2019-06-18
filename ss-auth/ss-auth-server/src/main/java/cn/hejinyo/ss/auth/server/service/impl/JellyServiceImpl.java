package cn.hejinyo.ss.auth.server.service.impl;

import cn.hejinyo.ss.auth.server.dto.AuthCheckResult;
import cn.hejinyo.ss.auth.server.feign.JellySysUserService;
import cn.hejinyo.ss.auth.server.model.vo.JellyUserInfoVO;
import cn.hejinyo.ss.auth.server.service.JellyService;
import cn.hejinyo.ss.common.consts.CommonConstant;
import cn.hejinyo.ss.common.framework.consts.StatusCode;
import cn.hejinyo.ss.common.framework.exception.InfoException;
import cn.hejinyo.ss.common.framework.utils.JwtTools;
import cn.hejinyo.ss.common.redis.utils.RedisUtils;
import cn.hejinyo.ss.common.utils.JsonUtil;
import cn.hejinyo.ss.common.utils.PojoConvertUtil;
import cn.hejinyo.ss.common.utils.Tools;
import cn.hejinyo.ss.jelly.model.dto.SysUserDTO;
import cn.hejinyo.ss.jelly.model.dto.UserNameLoginDTO;
import cn.hejinyo.ss.jelly.tools.JellyRedisKeys;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Supplier;

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
        return jellySysUserService.getByUserName(nameLoginVO.getUserName()).then(userDTO -> {
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
        //清除用户原来所有缓存
        RedisUtils.delete(JellyRedisKeys.storeUser(userId));
        // 设置新的token
        RedisUtils.hsetEX(JellyRedisKeys.storeUser(userId), JellyRedisKeys.USER_TOKEN, jti, CommonConstant.USER_TOKEN_EXPIRE);
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
            // 验证成功
            if (jti.equals(storeUserInfo.get(0))) {
                // 续命
                RedisUtils.expire(JellyRedisKeys.storeUser(userId), CommonConstant.USER_TOKEN_EXPIRE);
                result.setPass(true);
                result.setRoleSet(this.getInfoSet(userId, storeUserInfo.get(1), fields.get(1),
                        () -> jellySysUserService.getUserRoleSet(userId).get()));
                result.setPermSet(this.getInfoSet(userId, storeUserInfo.get(2), fields.get(2),
                        () -> jellySysUserService.getUserPermSet(userId).get()));
            }
        }
        return result;
    }

    private Set<String> getInfoSet(int userId, String str, String key, Supplier<? extends Set<String>> mapper) {
        Set<String> set;
        // redis中获得权限信息
        if (str != null) {
            return JsonUtil.parseObject(str, new TypeReference<Set<String>>() {
            }.getType());
        }
        // 不存在则数据库获得权限信息
        set = mapper.get();
        if (set != null) {
            set.removeIf(Objects::isNull);
            RedisUtils.hset(JellyRedisKeys.storeUser(userId), key, set);
            return set;
        }
        return new HashSet<>();
    }


    /**
     * 注销
     */
    @Override
    public void logout(String token) {
        try {
            AuthCheckResult result = this.getAuthUser(token);
            //token中获取用户名
            Integer userId = JwtTools.tokenInfo(token, JwtTools.JWT_TOKEN_USERID, Integer.class);
            if (result.isPass()) {
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
    public JellyUserInfoVO getUserInfo(String userToken) {
        // token中获取用户名
        AuthCheckResult authUser = this.getAuthUser(userToken);
        String userName = JwtTools.tokenInfo(userToken, JwtTools.JWT_TOKEN_USERNAME, String.class);
        return jellySysUserService.getByUserName(userName).then(userDto -> {
            JellyUserInfoVO userInfoVO = PojoConvertUtil.convert(userDto, JellyUserInfoVO.class);
            // 查询用户角色
            userInfoVO.setRoles(authUser.getRoleSet());
            // 查询用户权限
            userInfoVO.setPerms(authUser.getPermSet());
            // 查询菜单
            userInfoVO.setMenus(jellySysUserService.getUserMenus(userDto.getUserId(), userInfoVO.getRoles()).get());
            return userInfoVO;
        });
    }

    private AuthCheckResult getAuthUser(String userToken) {
        try {
            //验证token是否有效
            JwtTools.verifyToken(userToken, JwtTools.JWT_SIGN_KEY);
            AuthCheckResult result = this.checkToken(
                    JwtTools.tokenInfo(userToken, JwtTools.JWT_TOKEN_USERID, Integer.class),
                    JwtTools.tokenInfo(userToken, JwtTools.JWT_ID, String.class));
            if (result.isPass()) {
                return result;
            }
        } catch (Exception ignored) {
        }
        // 登录过期
        throw new InfoException(StatusCode.TOKEN_FAULT);
    }
}
