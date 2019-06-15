package cn.hejinyo.ss.jelly.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * sys_resource 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2019/06/15 17:16
 */
@Data
public class SysResourceDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 资源编号 res_id
     **/
    private Integer resId;

    /**
     * 父节点 parent_id
     **/
    private Integer parentId;

    /**
     * 资源类型（数据字典）：0：文件夹 ； 1：菜单； type
     **/
    private Integer type;

    /**
     * 资源名称 res_name
     **/
    private String resName;

    /**
     * 资源编码 res_code
     **/
    private String resCode;

    /**
     * 显示图标 icon
     **/
    private String icon;

    /**
     * 扩展信息
     */
    private ResourecMetaDTO meta;

    /**
     * 排序号 seq
     **/
    private Integer seq;

    /**
     * 状态(数据字典） 0：正常；1：禁用 state
     **/
    private Integer state;

    /**
     * 子节点
     */
    private List<SysResourceDTO> children;
}
