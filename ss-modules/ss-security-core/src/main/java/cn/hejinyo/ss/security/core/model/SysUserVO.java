package cn.hejinyo.ss.security.core.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/19 23:16
 */
public class SysUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户编号 user_id
     **/
    private Integer userId;

    /**
     * 用户昵称 nick_name
     **/
    private String nickName;

    /**
     * 用户名 user_name
     **/
    private String userName;

    /**
     * 用户密码 user_pwd
     **/
    private String userPwd;

    /**
     * 用户盐 user_salt
     **/
    private String userSalt;

    /**
     * 头像 avatar
     **/
    private String avatar;

    /**
     * 邮箱 email
     **/
    private String email;

    /**
     * 手机号 phone
     **/
    private String phone;

    /**
     * 最后登录IP login_ip
     **/
    private String loginIp;

    /**
     * 最后登录时间 login_time
     **/
    private Date loginTime;
}
