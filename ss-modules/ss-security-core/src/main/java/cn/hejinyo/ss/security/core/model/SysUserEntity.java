package cn.hejinyo.ss.security.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/19 22:51
 */
@Data
@Entity
@Table(name = "sys_user")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
public class SysUserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户编号 user_id
     **/
    @Id
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

    /**
     * 用户状态 0：正常；1：禁用； state
     **/
    private Integer state;

    /**
     * 注册时间 create_time
     **/
    private Date createTime;

    /**
     * 创建人员 create_id
     **/
    private Integer createId;

    /**
     * 修改时间 update_time
     **/
    private Date updateTime;

    /**
     * 更新人编号 update_id
     **/
    private Integer updateId;

}
