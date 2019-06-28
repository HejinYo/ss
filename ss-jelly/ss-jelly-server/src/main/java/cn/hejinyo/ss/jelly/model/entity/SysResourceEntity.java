package cn.hejinyo.ss.jelly.model.entity;

import java.io.Serializable;
import java.util.Date;

import cn.hejinyo.ss.common.model.validator.RestfulValid;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * sys_resource 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2019/06/15 17:16
 */
@Data
public class SysResourceEntity implements Serializable {
    /**
     * 资源编号 res_id
     **/
    @NotNull(message = "资源编号不能为空", groups = {RestfulValid.PUT.class})
    private Integer resId;

    /**
     * 父节点 parent_id
     **/
    @NotNull(message = "父节点不能为空", groups = {RestfulValid.POST.class, RestfulValid.PUT.class})
    private Integer parentId;

    /**
     * 资源类型（数据字典）：0：文件夹 ； 1：菜单； type
     **/
    private Integer type;

    /**
     * 资源名称 res_name
     **/
    @NotBlank(message = "资源名称不能为空", groups = {RestfulValid.POST.class, RestfulValid.PUT.class})
    private String resName;

    /**
     * 资源编码 res_code
     **/
    @NotBlank(message = "资源编码不能为空", groups = {RestfulValid.POST.class, RestfulValid.PUT.class})
    private String resCode;

    /**
     * 显示图标 icon
     **/
    private String icon;

    /**
     * 扩展信息 meta
     **/
    private String meta;

    /**
     * 排序号 seq
     **/
    private Integer seq;

    /**
     * 状态(数据字典） 0：正常；1：禁用 state
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
