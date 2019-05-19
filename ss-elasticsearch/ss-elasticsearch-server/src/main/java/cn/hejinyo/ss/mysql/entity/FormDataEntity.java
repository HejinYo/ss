package cn.hejinyo.ss.mysql.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * form_data 实体类
 * 
 * @author : HejinYo   hejinyo@gmail.com 
 * @date : 2019/05/19 15:22
 */
@Data
public class FormDataEntity implements Serializable {
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

    private static final long serialVersionUID = 1L;
}