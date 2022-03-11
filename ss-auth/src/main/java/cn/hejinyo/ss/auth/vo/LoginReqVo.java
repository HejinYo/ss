package cn.hejinyo.ss.auth.vo;

import lombok.Data;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/3/11
 */
@Data
public class LoginReqVo {
    /**
     *用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
