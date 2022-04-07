package cn.hejinyo.ss.admin.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/4/6 22:33
 */
@Getter
@Setter
@Entity
public class UserEntity {

    /**
     * 用户编号 user_id
     **/
    @Id
    @GeneratedValue
    private Long userId;

    /**
     * 用户名 user_name
     **/
    private String username;

    /**
     * 用户昵称
     **/
    private String nickname;

    /**
     * 用户密码 password
     **/
    private String password;

    /**
     * 用户加密盐 salt
     **/
    private String salt;

    /**
     * 用户状态 0=禁用；1=正常； state
     **/
    private Integer state;

    /**
     * 数据状态 0=删除 1=正常 common_state
     */
    private Integer commonState;

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
