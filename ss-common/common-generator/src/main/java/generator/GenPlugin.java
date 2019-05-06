package generator;

import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.io.File;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/27 12:41
 * @Description :
 */
public class GenPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }


    /**
     * 生成dao
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        interfaze.addJavaDocLine("/**");
        interfaze.addJavaDocLine(" * " + introspectedTable.getFullyQualifiedTable() + " 持久化层");
        interfaze.addJavaDocLine(" * ");
        interfaze.addJavaDocLine(" * @author : HejinYo   hejinyo@gmail.com ");
        interfaze.addJavaDocLine(" * @date : " + new SimpleDateFormat("yyyy/MM/dd HH:mm").format(new Date()));
        interfaze.addJavaDocLine(" */");


        String typeName = "Integer";
        if (introspectedTable.getPrimaryKeyColumns().size() == 1) {
            if (!introspectedTable.getPrimaryKeyColumns().get(0).getJdbcTypeName().toLowerCase().contains("integer")) {
                typeName = "String";
            }
        }
        //引入cn.hejinyo.base.BaseDao
        interfaze.addImportedType(new FullyQualifiedJavaType("cn.hejinyo.jelly.common.base.BaseDao"));
        // 添加base接口 extends BaseDao<User>
        interfaze.addSuperInterface(new FullyQualifiedJavaType("BaseDao<" + introspectedTable.getBaseRecordType() + "," + typeName + ">"));
        // 添加@Mapper注解
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Mapper"));
        interfaze.addAnnotation("@Mapper");
        //Dao接口不生成任何方法
        interfaze.getMethods().clear();
        return true;
    }

    /**
     * 不生成Setter方法
     */
    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        //return super.modelSetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
        return false;
    }

    /**
     * 不生成Getter方法
     */
    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        //return super.modelGetterMethodGenerated(method, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
        return false;
    }

    /**
     * 增加属性注释
     */
    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        //field.addJavaDocLine("//" + introspectedColumn.getRemarks());
        if (introspectedColumn.getRemarks() != null) {
            field.addJavaDocLine("/**\n\t * " + introspectedColumn.getRemarks() + " " + introspectedColumn.getActualColumnName() + "\n\t **/");
        }
        return super.modelFieldGenerated(field, topLevelClass, introspectedColumn, introspectedTable, modelClassType);
    }

    /**
     * 添加setter,getter方法注解
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * " + introspectedTable.getFullyQualifiedTable() + " 实体类");
        topLevelClass.addJavaDocLine(" * ");
        topLevelClass.addJavaDocLine(" * @author : HejinYo   hejinyo@gmail.com ");
        topLevelClass.addJavaDocLine(" * @date : " + new SimpleDateFormat("yyyy/MM/dd HH:mm").format(new Date()));
        topLevelClass.addJavaDocLine(" */");

        topLevelClass.addAnnotation("@Data");
        topLevelClass.addImportedType("lombok.Data");
        //topLevelClass.addImportedType("java.io.Serializable");
        //topLevelClass.addSuperInterface(new FullyQualifiedJavaType("Serializable"));
        //makeSerializable(topLevelClass, introspectedTable);
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    private void makeSerializable(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        Field field = new Field();
        field.setFinal(true);
        field.setInitializationString("1L");
        field.setName("serialVersionUID");
        field.setStatic(true);
        field.setType(new FullyQualifiedJavaType("long"));
        field.setVisibility(JavaVisibility.PRIVATE);
        context.getCommentGenerator().addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);
    }

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        return true;
    }


    private String getColumnList(IntrospectedTable introspectedTable) {
        // 表字段列表Column_List
        StringBuilder columnSQL = new StringBuilder();
        // java字段名
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            columnSQL.append(columnName).append(", ");
        }
        //生成表字段列
        return columnSQL.substring(0, columnSQL.lastIndexOf(","));
    }

    private String getWhereList(IntrospectedTable introspectedTable) {
        StringBuilder whereList = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            String javaProperty = introspectedColumn.getJavaProperty();
            if (!introspectedColumn.isAutoIncrement()) {
                whereList.append("\t\t<if test=\"null != ").append(javaProperty).append("\">");
                whereList.append("and ").append(columnName).append(" = #{").append(javaProperty).append("}</if>\n");
            }
        }
        String whereSQL = MessageFormat.format("<where>\n{0}\t</where>", whereList.toString());
        return whereSQL;
    }

    private String getWhereQueryList(IntrospectedTable introspectedTable, String type) {
        StringBuilder whereList = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            System.out.println("introspectedColumn.getJdbcTypeName():" + "TIMESTAMP" + introspectedColumn.getJdbcTypeName());
            String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            String javaProperty = introspectedColumn.getJavaProperty();
            if (!introspectedColumn.isAutoIncrement()) {
                whereList.append("\t\t<if test=\"").append(javaProperty).append(" != null and !&quot;&quot;.equals(").append(javaProperty).append(")\">");
                if ("like".equals(type)) {
                    if ("TIMESTAMP".equalsIgnoreCase(introspectedColumn.getJdbcTypeName())) {
                        whereList.append("and DATE_FORMAT(").append(columnName).append(",'%Y-%m-%d') = STR_TO_DATE(#{").append(javaProperty).append("},'%Y-%m-%d')</if>\n");
                    } else if ("VARCHAR".equalsIgnoreCase(introspectedColumn.getJdbcTypeName())) {
                        whereList.append("and ").append(columnName).append(" like concat('%',#{").append(javaProperty).append("},'%')</if>\n");
                    } else {
                        whereList.append("and ").append(columnName).append(" = #{").append(javaProperty).append("}</if>\n");
                    }

                } else {
                    whereList.append("and ").append(columnName).append(" = #{").append(javaProperty).append("}</if>\n");
                }
            }
        }
        return MessageFormat.format("<where>\n{0}\t</where>", whereList.toString());
    }

    private String getSaveColumn(IntrospectedTable introspectedTable, String itemName) {
        itemName = "".equals(itemName) ? "" : itemName + ".";
        StringBuilder saveColumn = new StringBuilder("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            String javaProperty = introspectedColumn.getJavaProperty();
            if (!introspectedColumn.isAutoIncrement()) {
                saveColumn.append("\t\t\t<if test=\"null != " + itemName).
                        append(javaProperty).append("\">").append(columnName).append(", </if>\n");
            }
        }
        return saveColumn.append("\t\t</trim>\n").toString();
    }

    private String getSaveValue(IntrospectedTable introspectedTable, String itemName) {
        itemName = "".equals(itemName) ? "" : itemName + ".";
        StringBuilder saveValue = new StringBuilder("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            String javaProperty = introspectedColumn.getJavaProperty();
            if (!introspectedColumn.isAutoIncrement()) {
                saveValue.append("\t\t\t<if test=\"null != " + itemName).append(javaProperty).append("\">").append("#{" + itemName).append(javaProperty)
                        .append("}, </if>\n");
            }
        }
        return saveValue.append("\t\t</trim>").toString();
    }

    private String getUpdateColumn(IntrospectedTable introspectedTable) {
        StringBuilder updateSQL = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            String javaProperty = introspectedColumn.getJavaProperty();
            // 拼接SQL
            if (!introspectedColumn.isAutoIncrement()) {
                updateSQL.append("\t\t\t<if test=\"null != ").append(javaProperty).append("\">");
                updateSQL.append(columnName).append(" = #{").append(javaProperty).append("}").append(", </if>\n");
            }
        }
        return updateSQL.toString();
    }

    /**
     * Base_Column_List,sql 字段列表
     */
    private XmlElement createSql(String id, String sqlStr) {
        XmlElement sql = new XmlElement("sql");
        sql.addAttribute(new Attribute("id", id));
        sql.addElement(new TextElement(sqlStr));
        return sql;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement oldElement = document.getRootElement();
        System.out.println("oldElement.getName():" + oldElement.getName());
        System.out.println("oldElement.getAttributes().get(0).getValue():" + oldElement.getAttributes().get(0).getValue());
        System.out.println("introspectedTable.getBaseRecordType():" + introspectedTable.getBaseRecordType());
        List<Element> oldList = oldElement.getElements();
        for (Element e : oldList) {
            e.getFormattedContent(10);
            System.out.println("e.getFormattedContent(1):" + e.getFormattedContent(1));
        }
        Element BaseResultMap = oldList.get(0);
        Element BaseColumnList = oldList.get(1);

        XmlElement rootElement = new XmlElement(oldElement.getName());
        Attribute attribute = new Attribute(oldElement.getAttributes().get(0).getName(), oldElement.getAttributes().get(0).getValue());

        rootElement.addAttribute(attribute);
        document.setRootElement(rootElement);
        rootElement.addElement(new TextElement("\n  <!-- 实体映射 -->"));
        rootElement.addElement(BaseResultMap);
        rootElement.addElement(new TextElement("\n  <!-- 表基本列 -->"));
        rootElement.addElement(BaseColumnList);

        //实体类,cn.hejinyo.model.SysUser
        String modelName = introspectedTable.getBaseRecordType();
        // 数据库表名 sys_user
        String tableName = introspectedTable.getFullyQualifiedTableNameAtRuntime();
        // 主键
        IntrospectedColumn pkColumn = introspectedTable.getPrimaryKeyColumns().get(0);
        //主键表字段名称 user_id
        String pkcolumnName = pkColumn.getActualColumnName();
        //主键实体属性名 userId
        String getJavaProperty = pkColumn.getActualColumnName();
        //所有的表字段列表 user_id, user_name, user_pwd, user_salt, email, phone, login_ip, login_time, state,  create_time, create_id
        String columnList = getColumnList(introspectedTable);
        String whereList = getWhereQueryList(introspectedTable, "");
        String saveColumn = getSaveColumn(introspectedTable, "");
        String saveValue = getSaveValue(introspectedTable, "");
        String updateColumn = getUpdateColumn(introspectedTable);
        String pkType = getParameterType(pkColumn);
        //rootElement.addElement(createSql("Base_Column_List", columnList));

        rootElement.addElement(new TextElement("\n  <!-- 通用查询sql -->"));
        rootElement.addElement(createSql("Base_Where_SQL", whereList));


        rootElement.addElement(new TextElement("\n  <!-- 插入一条记录 -->"));
        rootElement.addElement(createSave("save", tableName, modelName, saveColumn, saveValue, pkColumn));

        rootElement.addElement(new TextElement("\n  <!-- 更新一条记录 -->"));
        rootElement.addElement(createUpdate("update", tableName, modelName, updateColumn, pkColumn));

        rootElement.addElement(new TextElement("\n  <!-- 删除给定主键的记录 -->"));
        rootElement.addElement(createDels("delete", tableName, pkColumn, ""));
        rootElement.addElement(new TextElement("\n  <!-- 多个主键批量删除记录 -->"));
        rootElement.addElement(createDels("deleteBatch", tableName, pkColumn, "array"));
        rootElement.addElement(new TextElement("\n  <!-- 多个实体批量删除记录 -->"));
        rootElement.addElement(createDels("deleteList", tableName, pkColumn, "list"));

        rootElement.addElement(new TextElement("\n  <!-- 主键查找一条记录 -->"));
        rootElement.addElement(createSelect("findOne", tableName, modelName, pkColumn));
        rootElement.addElement(new TextElement("\n  <!-- 查询多条 -->"));
        rootElement.addElement(createSelect("findList", tableName, modelName, pkColumn));
        rootElement.addElement(new TextElement("\n  <!-- 分页查询 -->"));
        rootElement.addElement(createPageSelect(tableName, modelName, introspectedTable));
        rootElement.addElement(new TextElement("\n  <!-- 查询记录数量 -->"));
        rootElement.addElement(createSelect("count", tableName, modelName, pkColumn));
        rootElement.addElement(new TextElement("\n  <!-- 查询记录是否存在 -->"));
        rootElement.addElement(createSelect("exsit", tableName, modelName, pkColumn));
        rootElement.addElement(new TextElement("\n"));

        /* rootElement.addElement(new TextElement("\n  <!-- 查询所有 -->"));
         rootElement.addElement(createSelect("findAll", tableName, modelName, pkColumn));*/

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    /**
     * 查询
     */
    private XmlElement createSelect(String id, String tableName, String modelName, IntrospectedColumn pkColumn) {
        XmlElement select = new XmlElement("select");
        select.addAttribute(new Attribute("id", id));
        StringBuilder selectStr = new StringBuilder();
        if ("count".equals(id)) {
            /* select.addAttribute(new Attribute("parameterType", modelName));*/
            select.addAttribute(new Attribute("resultType", "int"));
            selectStr.append("select\n\t\t").append("count(*)").append("\n\t").append("from ").append(tableName);
            selectStr.append("\n\t\t").append("<include refid=\"Base_Where_SQL\" />");
            select.addElement(new TextElement(selectStr.toString()));
        } else if ("exsit".equals(id)) {
            /* select.addAttribute(new Attribute("parameterType", modelName));*/
            select.addAttribute(new Attribute("resultType", "boolean"));
            selectStr.append("select\n\t\t").append("count(*)").append("\n\t").append("from ").append(tableName);
            selectStr.append("\n\t\t").append("<include refid=\"Base_Where_SQL\" />");
            select.addElement(new TextElement(selectStr.toString()));
        } else {
            select.addAttribute(new Attribute("resultMap", "BaseResultMap"));
            selectStr.append("select\n\t\t").append("<include refid=\"Base_Column_List\" />").append("\n\t").append("from ").append(tableName);
            if ("findOne".equals(id)) {
                /* select.addAttribute(new Attribute("parameterType", getParameterType(pkColumn)));*/
                selectStr.append("\n\t").append("where ").append(pkColumn.getActualColumnName()).append(" = ").append("#{").append(pkColumn.getJavaProperty()).append("}");
                select.addElement(new TextElement(selectStr.toString()));
            }
            if ("findAll".equals(id)) {
                select.addElement(new TextElement(selectStr.toString()));
            }
            if ("findList".equals(id)) {
                /*  select.addAttribute(new Attribute("parameterType", modelName));*/
                selectStr.append("\n\t\t").append("<include refid=\"Base_Where_SQL\" />");
                select.addElement(new TextElement(selectStr.toString()));
            }
        }
        return select;
    }

    /**
     * 查询
     */
    private XmlElement createPageSelect(String tableName, String modelName, IntrospectedTable introspectedTable) {
        XmlElement select = new XmlElement("select");
        select.addAttribute(new Attribute("id", "findPage"));
        select.addAttribute(new Attribute("resultMap", "BaseResultMap"));
        StringBuilder selectStr = new StringBuilder();
        selectStr.append("select\n\t\t").append("<include refid=\"Base_Column_List\" />").append("\n\t").append("from ").append(tableName);
        /*select.addAttribute(new Attribute("parameterType", "cn.hejinyo.utils.PageQuery"));*/
        selectStr.append("\n\t\t").append(getWhereQueryList(introspectedTable, "like"));
        select.addElement(new TextElement(selectStr.toString()));
        return select;
    }

    /**
     * 获得主键java类型
     *
     * @param pkColumn
     * @return
     */
    private String getParameterType(IntrospectedColumn pkColumn) {
        String typeName = Integer.class.getName();
        if (!pkColumn.getJdbcTypeName().toLowerCase().contains("integer")) {
            typeName = String.class.getName();
        }
        return typeName;
    }

    /**
     * 保存
     */
    private XmlElement createSave(String id, String tableName, String modelName, String saveColumn, String saveValue, IntrospectedColumn pkColumn) {
        XmlElement save = new XmlElement("insert");
        save.addAttribute(new Attribute("id", id));
        if (null != pkColumn) {
            save.addAttribute(new Attribute("useGeneratedKeys", "true"));
            save.addAttribute(new Attribute("keyProperty", pkColumn.getJavaProperty()));
            /*save.addAttribute(new Attribute("parameterType", modelName));*/
            save.addElement(new TextElement("insert into " + tableName + "\n" + saveColumn +
                    "\tvalues\n" + saveValue));
        } else {
            StringBuilder saveList = new StringBuilder("insert into ").append(tableName).append("\n").append(saveColumn)
                    .append("\tvalues\n")
                    .append("\t<foreach collection=\"list\" index=\"index\" item=\"item\" separator=\",\"> \n\t")
                    .append(saveValue)
                    .append("\n\t</foreach>");
            save.addElement(new TextElement(saveList.toString()));
        }
        return save;
    }

    /**
     * 更新
     */
    private XmlElement createUpdate(String id, String tableName, String modelName, String updateColumn, IntrospectedColumn pkColumn) {
        XmlElement update = new XmlElement("update");
        update.addAttribute(new Attribute("id", id));
        /*update.addAttribute(new Attribute("parameterType", modelName));*/
        if ("update".equals(id)) {
            StringBuffer updateSQL = new StringBuffer("update ").append(tableName);
            updateSQL.append(" \n\t\t<set>\n").append(updateColumn).append("\t\t</set>");
            updateSQL.append("\n\t\twhere ").append(pkColumn.getActualColumnName()).append(" = ")
                    .append("#{").append(pkColumn.getJavaProperty()).append("}");
            update.addElement(new TextElement(updateSQL.toString()));
        }
        return update;
    }

    /**
     * 删除
     */
    private XmlElement createDels(String method, String tableName, IntrospectedColumn pkColumn, String type) {
        XmlElement delete = new XmlElement("delete");
        delete.addAttribute(new Attribute("id", method));
        StringBuilder deleteStr = new StringBuilder("delete\n\t\tfrom ").append(tableName).append("\n\twhere").append("\n\t\t");
        if ("".equals(type)) {
            /*delete.addAttribute(new Attribute("parameterType", getParameterType(pkColumn)));*/
            deleteStr.append(pkColumn.getActualColumnName()).append(" = ").append("#{" + pkColumn.getJavaProperty() + "}");
            delete.addElement(new TextElement(deleteStr.toString()));
            return delete;
        } else if (type.contains("array")) {
            /*delete.addAttribute(new Attribute("parameterType", List.class.getName()));*/
            deleteStr.append(pkColumn.getActualColumnName()).append(" in ").append("\n\t\t")
                    .append("<foreach collection=\"array\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">")
                    .append("\n\t\t\t#{item}")
                    .append("\n\t\t</foreach>");
            delete.addElement(new TextElement(deleteStr.toString()));
            return delete;
        } else if (type.contains("list")) {
            /*delete.addAttribute(new Attribute("parameterType", List.class.getName()));*/
            deleteStr.append(pkColumn.getActualColumnName()).append(" in ").append("\n\t\t")
                    .append("<foreach collection=\"list\" index=\"index\" item=\"item\" open=\"(\" separator=\",\" close=\")\">")
                    .append("\n\t\t\t#{item." + pkColumn.getJavaProperty() + "}")
                    .append("\n\t\t</foreach>");
            delete.addElement(new TextElement(deleteStr.toString()));
            return delete;
        }
        return delete;
    }

    public static void main(String[] args) {
        //delAllFile(System.getProperty("user.dir") + "/ss-common/common-generator/src/main/java/generator/dao");
        generate();
    }

    private static void generate() {
        String config = GenPlugin.class.getClassLoader().getResource("generator/generator.xml").getFile();
        String[] arg = {"-configfile", config, "-overwrite"};
        ShellRunner.main(arg);
    }


    /*************************删除文件夹delFolder / 删除文件夹中的所有文件delAllFile *start***********/

    /**
     * 删除文件夹
     */
    public static void delFolder(String folderPath) {
        try {
            //删除完里面所有内容
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath;
            java.io.File myFilePath = new java.io.File(filePath);
            //删除空文件夹
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除指定文件夹下所有文件
     */
    private static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp;
        for (String s : tempList) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + s);
            } else {
                temp = new File(path + File.separator + s);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                //先删除文件夹里面的文件
                delAllFile(path + "/" + s);
                //再删除空文件夹
                delFolder(path + "/" + s);
                flag = true;
            }
        }
        return flag;
    }

}

