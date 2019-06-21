package cn.hejinyo.ss.jelly.model.dto;

import lombok.Data;

/**
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/06/22 01:12
 */
@Data
public class SysPermissionPageQueryDTO {
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
}
