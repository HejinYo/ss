package cn.hejinyo.ss.admin.entity;

import cn.hejinyo.ss.admin.constant.UserStateEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/4/6 22:33
 */
@Getter
@Setter
@Entity
@Table(name = "ss_user", schema = "ss-admin")
public class UserEntity {

    /**
     * 用户编号 user_id
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    /**
     * 用户名 user_name
     **/
    @Column(length = 100)
    private String username;

    /**
     * 用户昵称
     **/
    @Column(length = 200)
    private String nickname;

    /**
     * 用户密码 password
     **/
    @Column(length = 200)
    private String password;

    /**
     * 用户加密盐 salt
     **/
    @Column(length = 100)
    private String salt;

    /**
     * 用户状态 0=禁用；1=正常 2=锁定； state
     **/
    private UserStateEnum state;

    /**
     * 数据状态 0=删除 1=正常 data_state
     */
    private Integer dataState;

    /**
     * 创建时间 create_time
     **/
    private Date createTime;

    /**
     * 创建人员ID create_id
     **/
    private Integer createId;

    /**
     * 修改时间 update_time
     **/
    private Date updateTime;

    /**
     * 修改人员ID update_id
     **/
    private Integer updateId;

}
