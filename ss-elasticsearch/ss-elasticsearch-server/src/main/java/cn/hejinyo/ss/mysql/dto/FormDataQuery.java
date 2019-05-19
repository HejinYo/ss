package cn.hejinyo.ss.mysql.dto;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/5/19 17:05
 */
@Data
public class FormDataQuery {

    private String sort;

    private String sidx;

    private HashMap<String, FormDataParam> dataParam;

    /**
     * 数据编号 SysNo
     **/
    private Integer sysno;

    /**
     * 企业编号 OrganizationCode
     **/
    private String organizationcode;

    /**
     * 表单编号 FormSysNo
     **/
    private Integer formsysno;

    /**
     * 数据内容 Data
     **/
    private String data;

    /**
     * 流程状态0无流程，1草稿，2发起，3结束 FlowStatus
     **/
    private Boolean flowstatus;

    /**
     * 数据状态 CommonStatus
     **/
    private Boolean commonstatus;

    /**
     * 创建者系统编号 InUserSysNo
     **/
    private Integer inusersysno;

    /**
     * 创建者显示名 InUserName
     **/
    private String inusername;

    /**
     * 创建时间 InDate
     **/
    private Date indate;

    /**
     * 最后修改人系统编号 EditUserSysNo
     **/
    private Integer editusersysno;

    /**
     * 最后修改人显示名 EditUserName
     **/
    private String editusername;

    /**
     * 更新时间 EditDate
     **/
    private Date editdate;
}
