package cn.hejinyo.ss.common.framework.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/05/04 19:04
 */
public class JwtTools {

    /**
     * 请求头中 JWT 的key
     */
    public static final String AUTHOR_PARAM = "x-access-token";

    /**
     * JWT 签名
     */
    public static final String JWT_SIGN_KEY = "jwt_hejinyo";

    /**
     * JWT token 用户名
     */
    public static final String JWT_TOKEN_USERNAME = "jtu";

    /**
     * JWT token 用户编号
     */
    public static final String JWT_TOKEN_USERID = "uid";

    /**
     * JWT 单用户登录
     */
    public static final String JWT_SINGLE_USER = "seu";

    /**
     * JWT_颁发给主体
     */
    public static final String JWT_SUB = "sub";

    /**
     * JWT_编号
     */
    public static final String JWT_ID = "jti";


    /**
     * 创建jwt token
     * expires n小时后失效
     * <p>
     * iss: jwt签发者
     * sub: jwt所面向的用户
     * aud: 接收jwt的一方
     * exp: jwt的过期时间，这个过期时间必须要大于签发时间
     * nbf: 定义在什么时间之前，该jwt都是不可用的.
     * iat: jwt的签发时间
     * jti: jwt的唯一身份标识，主要用来作为一次性token,从而回避重放攻击。
     */
    public static String createToken(String subject, boolean singleUser, int userId, String username, String key, int expires) {
        return JWT.create()
                // 签发时间
                .withIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                // 过期时间
                .withExpiresAt(Date.from(LocalDateTime.now().plus(expires, ChronoUnit.HOURS).atZone(ZoneId.systemDefault()).toInstant()))
                // 颁发给主体
                .withSubject(subject)
                // jwt 的id
                .withJWTId(UUID.randomUUID().toString())
                // 是否单用户登录
                .withClaim(JWT_SINGLE_USER, singleUser)
                // 用户id
                .withClaim(JWT_TOKEN_USERID, userId)
                // 用户名
                .withClaim(JWT_TOKEN_USERNAME, username)
                // 加密方式
                .sign(Algorithm.HMAC256(key));
    }

    /**
     * 验证token 有效性
     */
    public static void verifyToken(String token, String password) throws UnsupportedEncodingException {
        JWT.require(Algorithm.HMAC256(password)).build().verify(token);
    }


    /**
     * 获得token 信息
     */
    @SuppressWarnings("unchecked")
    public static <T> T tokenInfo(String token, String key, Class<T> clazz) {
        Claim claim = JWT.decode(token).getClaim(key);
        switch (clazz.getSimpleName()) {
            case "Boolean":
                return (T) claim.asBoolean();
            case "Integer":
                return (T) claim.asInt();
            default:
                return (T) claim.asString();
        }
    }


    /**************************** 测试 *********************************/
    public static void main(String agrs[]) {
        System.out.println(createToken("jelly", false, 1, "hejinyo", "jwt_hejinyo", 24));
        /*String[] str = encryptDBPassword("");
        for (String s : str) {
            System.out.println(s);
        }*/
    }
}
