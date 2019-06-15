package cn.hejinyo.ss.jelly.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/06/16 00:40
 */
@Data
public class ResourecMetaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 访问路径
     */
    private String path;

    /**
     * 重定向
     */
    private String redirect;

    /**
     * 访问方式
     */
    private String target;

    /**
     * 前端组件
     */
    private String component;

    /**
     * 缓存
     */
    private Boolean keepAlive;

    /**
     * 隐藏标题
     */
    private Boolean hideHeader;

    /**
     * 隐藏子菜单
     */
    private Boolean hideChildrenInMenu;

}
