package cn.hejinyo.ss.auth.service;

import cn.hejinyo.ss.auth.vo.SsAuthLoginReqVo;
import cn.hejinyo.ss.auth.vo.SsAuthLoginTokenVo;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/3/27 18:50
 */
public interface SsAuthLoginService {

    /**
     * 用户登陆返回token
     *
     * @param ssAuthLoginReqVo SsAuthLoginReqVo
     * @return SsAuthLoginTokenVo
     */
    SsAuthLoginTokenVo login(SsAuthLoginReqVo ssAuthLoginReqVo);

    /**
     * 微服务获取 jwkSet
     *
     * @return String
     */
    String jwkSet();

    /**
     * 验证是否登陆并换取微服务 MsToken
     *
     * @param accessToken String
     * @return String
     */
    String checkAndGetMsToken(String accessToken);
}
