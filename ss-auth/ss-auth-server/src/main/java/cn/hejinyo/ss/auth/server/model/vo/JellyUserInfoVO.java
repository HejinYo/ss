package cn.hejinyo.ss.auth.server.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * jelly用户信息
 *
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/06/14 21:09
 */
@Data
public class JellyUserInfoVO implements Serializable {

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

    /**
     * 用户角色字符串列表
     */
    private Set<String> roles;

    /**
     * 用户权限字符串列表
     */
    private Set<String> perms;

    /**
     * 用户菜单字符串列表
     */
    private List<String> menus;


}
