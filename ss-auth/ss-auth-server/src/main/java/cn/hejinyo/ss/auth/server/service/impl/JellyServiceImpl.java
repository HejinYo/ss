package cn.hejinyo.ss.auth.server.service.impl;

import cn.hejinyo.calm.common.redis.utils.RedisKeys;
import cn.hejinyo.calm.common.redis.utils.RedisUtils;
import cn.hejinyo.calm.common.web.utils.ShiroUtils;
import cn.hejinyo.ss.auth.server.dto.AuthCheckResult;
import cn.hejinyo.ss.auth.server.feign.JellySysUserService;
import cn.hejinyo.ss.auth.server.service.JellyService;
import cn.hejinyo.ss.common.consts.Constant;
import cn.hejinyo.ss.common.consts.StatusCode;
import cn.hejinyo.ss.common.exception.InfoException;
import cn.hejinyo.ss.common.utils.SmsUtils;
import cn.hejinyo.ss.common.utils.StringUtils;
import cn.hejinyo.ss.common.utils.Tools;
import cn.hejinyo.ss.jelly.dto.PhoneLoginDTO;
import cn.hejinyo.ss.jelly.dto.SysUserDTO;
import cn.hejinyo.ss.jelly.dto.UserNameLoginDTO;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/9/2 18:55
 */
@Service
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
        //验证密码
        if (!userDTO.getUserPwd().equals(ShiroUtils.userPassword(nameLoginVO.getUserPwd(), userDTO.getUserSalt()))) {
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
        String token = Tools.createToken("jelly", true, userId, userDTO.getUserName(), Constant.JWT_SIGN_KEY, Constant.JWT_EXPIRES_DEFAULT);
        String jti = Tools.tokenInfo(token, Constant.JWT_ID, String.class);
        redisUtils.hset(RedisKeys.storeUser(userId), RedisKeys.USER_TOKEN, jti);
        return token;
    }

    /**
     * 查找用户编号对应的角色编码字符串
     */
    @Override
    public Set<String> getUserRoleSet(int userId) {
        // redis中获得角色信息
        Set<String> roleSet = redisUtils.hget(RedisKeys.storeUser(userId), RedisKeys.USER_ROLE, new TypeToken<Set<String>>() {
        }.getType());

        // 不存在则数据库获得角色信息
        if (roleSet == null) {
            roleSet = jellySysUserService.getUserRoleSet(userId);
        }
        if (roleSet != null) {
            roleSet.removeIf(Objects::isNull);
            redisUtils.hset(RedisKeys.storeUser(userId), RedisKeys.USER_ROLE, roleSet);
        }
        return roleSet;
    }

    /**
     * 查找用户编号对应的权限编码字符串
     */
    @Override
    public Set<String> getUserPermSet(int userId) {
        // redis中获得权限信息
        Set<String> permissionsSet = redisUtils.hget(RedisKeys.storeUser(userId), RedisKeys.USER_PERM, new TypeToken<Set<String>>() {
        }.getType());

        // 不存在则数据库获得权限信息
        if (permissionsSet == null) {
            permissionsSet = jellySysUserService.getUserPermSet(userId);
        }
        if (permissionsSet != null) {
            permissionsSet.removeIf(Objects::isNull);
            redisUtils.hset(RedisKeys.storeUser(userId), RedisKeys.USER_PERM, permissionsSet);
        }
        return permissionsSet;
    }

    /**
     * 验证token 通过返回角色权限信息
     */
    @Override
    public AuthCheckResult checkToken(Integer userId, String jti) {
        String checkJti = redisUtils.hget(RedisKeys.storeUser(userId), RedisKeys.USER_TOKEN, String.class);
        AuthCheckResult result = new AuthCheckResult();
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
            Tools.verifyToken(token, Constant.JWT_SIGN_KEY);
            //token中获取用户名
            Integer userId = Tools.tokenInfo(token, Constant.JWT_TOKEN_USERID, Integer.class);
            String jti = Tools.tokenInfo(token, Constant.JWT_ID, String.class);
            //查询缓存中的用户信息
            String checkJti = redisUtils.hget(RedisKeys.storeUser(userId), RedisKeys.USER_TOKEN, String.class);
            if (jti.equals(checkJti)) {
                //清除用户原来所有缓存
                redisUtils.delete(RedisKeys.storeUser(userId));
            }
        } catch (Exception ignored) {
        }
    }


    /**
     * 发送电话登录验证码
     */
    @Override
    public boolean sendPhoneCode(String phone) {
        // 验证电话号码格式
        String regEx = "^$|^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(phone);
        if (!matcher.find()) {
            throw new InfoException("电话号码格式错误");
        }
        String code = String.valueOf(RandomUtils.nextInt(100000, 999999));
        boolean result = SmsUtils.sendLogin(phone, code);
        if (result) {
            redisUtils.setEX(RedisKeys.smsSingle(phone), code, 300);
        }
        return result;
    }

    /**
     * 手机用户登录
     */
    @Override
    public SysUserDTO phoneLogin(PhoneLoginDTO phoneLogin) {
        String phone = phoneLogin.getPhone();
        String code = phoneLogin.getCode();
        // 匹配验证码
        String localCode = redisUtils.get(RedisKeys.smsSingle(phone), String.class);
        if (StringUtils.isEmpty(localCode)) {
            throw new InfoException("验证码过期，请重新获取");
        }
        if (!code.equals(localCode)) {
            throw new InfoException("验证码错误");
        }
        // 删除验证码
        redisUtils.delete(RedisKeys.smsSingle(phone));
        // 根据号码查询用户
        SysUserDTO userDTO = jellySysUserService.findByPhone(phone);
        // 如果无相关用户或已删除则返回null
        if (null == userDTO) {
            throw new InfoException(StatusCode.LOGIN_USER_NOEXIST);
        } else if (1 == userDTO.getState()) {
            throw new InfoException(StatusCode.LOGIN_USER_LOCK);
        }
        return userDTO;
    }
}
