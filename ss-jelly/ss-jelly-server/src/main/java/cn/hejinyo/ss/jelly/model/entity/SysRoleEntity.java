package cn.hejinyo.ss.jelly.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * sys_role 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2019/06/16 21:03
 */
@Data
public class SysRoleEntity implements Serializable {
    /**
     * 角色编号 role_id
     **/
    private Integer roleId;

    /**
     * 角色编码 role_code
     **/
    private String roleCode;

    /**
     * 角色名称 role_name
     **/
    private String roleName;

    /**
     * 角色描述 description
     **/
    private String description;

    /**
     * 排序号 seq
     **/
    private Integer seq;

    /**
     * 状态 0：正常；1：锁定；-1：禁用(删除) state
     **/
    private Integer state;

    /**
     * 创建人员 create_id
     **/
    private Integer createId;

    /**
     * 创建时间 create_time
     **/
    private Date createTime;

    /**
     * 修改人员 update_id
     **/
    private Integer updateId;

    /**
     * 修改时间 update_time
     **/
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
