package cn.hejinyo.ss.es.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * form_data 实体类
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2019/05/13 14:30
 */
@Data
@Document(indexName = "ibuild-form", type = "form-data")
public class FormDataEntity implements Serializable {
    /**
	 * 企业编号 OrganizationCode
	 **/
    private String organizationcode;

    /**
     * 数据编号 SysNo
     **/
    @Id
    @Field(type = FieldType.Integer)
    private Integer sysNo;

    /**
     * 表单编号 FormSysNo
     **/
    @Field(type = FieldType.Integer)
    private Integer formSysNo;

    /**
     * 数据内容 Data
     **/
    @Field(type = FieldType.Auto)
    private Object data;

    /**
     * 创建者显示名 InUserName
     **/
    @Field(type = FieldType.Text)
    private String inUserName;

    /**
     * 创建时间 InDate
     **/
    @Field(type = FieldType.Date)
    private Date inDate;

    private static final long serialVersionUID = 1L;
}