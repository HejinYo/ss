package cn.hejinyo.ss.jelly.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * sys_permission 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2019/06/16 20:44
 */
@Data
public class SysPermissionEntity implements Serializable {
    /**
     * 权限编号 perm_id
     **/
    private Integer permId;

    /**
     * 资源编号 res_id
     **/
    private Integer resId;

    /**
     * 权限名称 perm_name
     **/
    private String permName;

    /**
     * 权限编码 perm_code
     **/
    private String permCode;

    /**
     * 查询编码 code
     **/
    private String code;

    /**
     * 状态 0：禁用；1：正常 state
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
