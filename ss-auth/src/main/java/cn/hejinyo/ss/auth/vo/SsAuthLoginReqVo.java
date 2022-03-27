package cn.hejinyo.ss.auth.vo;

import lombok.Data;

/**
 * 登陆用户名密码
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/3/11
 */
@Data
public class SsAuthLoginReqVo {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
