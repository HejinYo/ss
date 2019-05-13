package ftl;

import config.GenConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/5/13 23:25
 */
public class FreeMarkerGen {

    private static final List<String> baseType = Arrays.asList("String", "Integer", "Double", "Float", "Byte", "Char");

    public static void build(Document document, IntrospectedTable introspectedTable) {
        Map<String, Object> data = new HashMap<>(16);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        // cn.hejinyo.ss.entity.SysUserEntity
        String baseRecordType = introspectedTable.getBaseRecordType();
        data.put("date", format.format(new Date()));
        data.put("baseRecordType", baseRecordType);
        data.put("packagePath", GenConfig.DTO_PACKAGE_PATH);
        // sys_user
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        String javaName = GenConfig.UnderlineToHump(tableName) + "DTO";
        data.put("tableName", tableName);
        data.put("javaName", javaName);
        HashSet<String> importStr = new HashSet<>();
        List<Map<String, Object>> fields = new ArrayList<>();
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            String javaProperty = introspectedColumn.getJavaProperty();
            String jdbcTypeName = introspectedColumn.getJdbcTypeName();
            String remarks = introspectedColumn.getRemarks();
            Map<String, Object> dataMap = new HashMap<>(16);
            // VARCHAR
            dataMap.put("jdbcTypeName", jdbcTypeName);
            // userName
            dataMap.put("javaProperty", javaProperty);
            // user_name
            dataMap.put("columnName", columnName);
            // 用户名
            dataMap.put("remarks", remarks);
            // java.lang.String
            String fullyQualifiedName = introspectedColumn.getFullyQualifiedJavaType().getFullyQualifiedName();
            dataMap.put("fullyQualifiedName", fullyQualifiedName);
            String javaType = introspectedColumn.getFullyQualifiedJavaType().getShortName();
            dataMap.put("javaType", javaType);
            if (!baseType.contains(javaType)) {
                importStr.add(fullyQualifiedName);
            }
            fields.add(dataMap);
        }
        data.put("import", importStr);
        data.put("fields", fields);
        genJavaFile(data, GenConfig.FILE_PATH + "/" + (GenConfig.DTO_PACKAGE_PATH.replace(".", "/")), javaName + ".java", GenConfig.TEMPLATE_PATH, "test.ftl");
    }

    public static void genJavaFile(Map<String, Object> dataMap, String filePath, String fileName, String templatePath, String templateName) {
        // step1 创建freeMarker配置实例
        System.out.println(filePath);
        System.out.println(filePath + "+" + fileName);
        Configuration configuration = new Configuration(DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        Writer out = null;
        try {
            // step2 获取模版路径
            configuration.setDirectoryForTemplateLoading(new File(templatePath));
            // step3 创建数据模型
            // step4 加载模版文件
            Template template = configuration.getTemplate(templateName);
            // step5 生成数据
            File docFile = new File(filePath + "/" + fileName);
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile)));
            // step6 输出文件
            template.process(dataMap, out);
            System.out.println(fileName + "文件创建成功 !");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.flush();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }


}
