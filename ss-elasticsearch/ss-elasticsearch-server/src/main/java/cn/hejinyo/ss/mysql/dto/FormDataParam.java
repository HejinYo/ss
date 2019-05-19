package cn.hejinyo.ss.mysql.dto;

import lombok.Data;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/5/19 17:04
 */
@Data
public class FormDataParam {

    private String key;

    private String queryKey;

    private Object value;

    private Integer type;
}
