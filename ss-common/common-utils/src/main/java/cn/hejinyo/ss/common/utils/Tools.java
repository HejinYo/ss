package cn.hejinyo.ss.common.utils;

import cn.hejinyo.ss.common.consts.Constant;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import jodd.util.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

/**
 * 字符串，字符，日期 工具类
 *
 * @author HejinYo hejinyo@gmail.com
 */
public class Tools {

    /**
     * BASE64编码
     */
    public static String base64Encode(String str) {
        return Base64.encodeToString(str);
    }

    /**
     * BASE64编码
     */
    public static String base64Encode(byte[] arr) {
        return Base64.encodeToString(arr);
    }

    /**
     * BASE64解码
     */
    public static String base64Decoder(String str) {
        return Base64.decodeToString(str);
    }

    /**
     * 数据库密码加密
     */
    public static String[] encryptDBPassword(String password) {
        String path = "C:/java/tools/JDK/";
        String druid = "druid-1.0.16.jar com.alibaba.druid.filter.config.ConfigTools ";
        String fileInfo = "java -cp " + path + druid + password + " ;exit;";
        String pw[] = new String[3];
        try {
            Process proc = Runtime.getRuntime().exec(fileInfo);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader in = new BufferedReader(is);
            //privateKey
            pw[1] = in.readLine();
            //publicKey
            pw[2] = in.readLine();
            //encryptPassword
            pw[0] = in.readLine();
            in.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pw;
    }

    /**
     * HmacSHA256 加密，返回 base64编码
     */
    public static String hmacSHA256Digest(String key, String content) {
        try {
            //创建加密对象
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            //处理密钥Key
            SecretKey secret_key = new SecretKeySpec(key.getBytes("utf-8"), "HmacSHA256");
            //初始化加密
            sha256_HMAC.init(secret_key);
            //加密内容
            byte[] doFinal = sha256_HMAC.doFinal(content.getBytes("utf-8"));
            //16进制内容
            // byte[] hexB = new Hex().encode(doFinal);
            return base64Encode(doFinal);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * byte 转 字符串
     */
    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) {
                hs.append('0');
            }
            hs.append(stmp);
        }
        return hs.toString();
    }


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
                .withClaim(Constant.JWT_SINGLE_USER, singleUser)
                // 用户id
                .withClaim(Constant.JWT_TOKEN_USERID, userId)
                // 用户名
                .withClaim(Constant.JWT_TOKEN_USERNAME, username)
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
