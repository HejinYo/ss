package cn.hejinyo.ss.auth.vo;

import cn.hejinyo.ss.auth.constant.TokenTypeEnum;
import lombok.Data;

import java.time.Instant;

/**
 * 登录返回token
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/3/15
 */
@Data
public class SsAuthLoginTokenVo {

    /**
     * token值
     */
    private String tokenValue;

    /**
     * token类型
     */
    private TokenTypeEnum tokenType;

    /**
     * 过期时间
     */
    private Instant expiresIn;
}
