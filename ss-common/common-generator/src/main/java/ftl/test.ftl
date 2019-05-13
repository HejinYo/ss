package ${packagePath};

import lombok.Data;
import java.io.Serializable;
<#list import as i>import ${i};</#list>

/**
* ${tableName}
*
* @author : HejinYo   hejinyo@gmail.com
* @date : ${date}
*/
@Data
public class ${javaName} implements Serializable {
    private static final long serialVersionUID = 1L;
<#list fields as f>

    /**
    * ${f.remarks}
    **/
    private ${f.javaType} ${f.javaProperty};
</#list>
}