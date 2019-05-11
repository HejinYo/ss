package config;

import com.alibaba.fastjson.JSON;
import com.mysql.jdbc.StringUtils;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static config.SqlIdEnum.*;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/27 12:41
 */
public class GenPlugin extends PluginAdapter {

    private static final String FOUR_BLANK = "    ";

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
        interfaze.addImportedType(new FullyQualifiedJavaType(BaseDao.class.getName()));
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
        return false;
    }

    /**
     * 不生成Getter方法
     */
    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return false;
    }

    /**
     * 增加属性注释
     */
    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        //field.addJavaDocLine("//" + introspectedColumn.getRemarks());
        if (!StringUtils.isNullOrEmpty(introspectedColumn.getRemarks())) {
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


    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        XmlElement oldElement = document.getRootElement();
        System.out.println("\n\n\n\n\n\n");
        System.out.println("接口全类名 ===>\t" + oldElement.getAttributes().get(0).getValue());
        System.out.println("实体类全类名 ===>\t" + introspectedTable.getBaseRecordType());

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
        System.out.println("实体类 ===>\t" + modelName);
        System.out.println("数据库表名 ===>\t" + tableName);
        System.out.println("主键表字段名称 ===>\t" + pkcolumnName);
        System.out.println("主键实体属性名 ===>\t" + getJavaProperty);

        // 参数类型
        String parameterType = "";
        // 結果集映射
        String resultMap = "";

        System.out.println("\n\n\n\n\n\n");

        HashMap<String, XmlElement> cotentMap = new HashMap<>();

        for (Element e : oldElement.getElements()) {
            if (e instanceof XmlElement) {
                XmlElement xmlElement = (XmlElement) e;
                if (xmlElement.getAttributes() != null) {
                    for (Attribute attribute : xmlElement.getAttributes()) {
                        // 获取ID名称
                        if (attribute.getName().equals(AttributesNameEnum.ID.getValue())) {
                            cotentMap.put(attribute.getValue(), xmlElement);
                        }

                        // 参数类型
                        if (attribute.getName().equals(AttributesNameEnum.PARAMETER_TYPE.getValue())) {
                            parameterType = attribute.getValue();
                        }

                        // 結果集映射
                        if (attribute.getName().equals(AttributesNameEnum.RESULT_MAP.getValue())) {
                            resultMap = attribute.getValue();
                        }

                    }
                }
            }
        }

        cotentMap.forEach((k, v) -> {
            System.out.println(k + "\n\t" + JSON.toJSONString(v) + "\n");
        });

        // 创建新的XML
        XmlElement rootElement = new XmlElement(oldElement.getName());
        document.setRootElement(rootElement);

        // xml 说明
        Attribute attribute = new Attribute(oldElement.getAttributes().get(0).getName(), oldElement.getAttributes().get(0).getValue());
        rootElement.addAttribute(attribute);

        // 实体映射
        SqlIdEnum baseResultEnum = BASE_RESULT_MAP;
        XmlElement baseResultMap = cotentMap.get(baseResultEnum.getValue());
        if (baseResultMap != null) {
            rootElement.addElement(new TextElement(xmlAnnotation(baseResultEnum.getState(), 1)));
            rootElement.addElement(baseResultMap);
        }

        // 表基本列
        SqlIdEnum baseColumnListEnum = BASE_COLUMN_LIST;
        XmlElement baseColumnList = cotentMap.get(baseColumnListEnum.getValue());
        if (baseColumnList != null) {
            rootElement.addElement(new TextElement(xmlAnnotation(baseColumnListEnum.getState(), 1)));
            rootElement.addElement(baseColumnList);
        }

        // 通用查询sql
        XmlElement baseWhereSql = new XmlElement("sql");
        baseWhereSql.addAttribute(new Attribute(AttributesNameEnum.ID.getValue(), BASE_WHERE_SQL.getNewValue()));
        baseWhereSql.addElement(new TextElement(bulidWhereQueryList(introspectedTable, false)));
        rootElement.addElement(new TextElement(xmlAnnotation(BASE_WHERE_SQL.getState(), 1)));
        rootElement.addElement(baseWhereSql);

        // 插入一条记录
        rootElement.addElement(new TextElement(xmlAnnotation(SAVE.getState(), 1)));
        rootElement.addElement(createSave(SAVE.getNewValue(), tableName, parameterType, introspectedTable, pkColumn));

        // 保存一条完整记录
        rootElement.addElement(new TextElement(xmlAnnotation(SAVE_FULL.getState(), 1)));
        rootElement.addElement(createSaveFull(SAVE_FULL.getNewValue(), tableName, parameterType, introspectedTable, pkColumn));

        // 更新一条记录
        rootElement.addElement(new TextElement(xmlAnnotation(UPDATE.getState(), 1)));
        rootElement.addElement(createUpdate(UPDATE.getNewValue(), tableName, parameterType, introspectedTable, pkColumn));

        // 更新一条完整记录
        rootElement.addElement(new TextElement(xmlAnnotation(UPDATE_FULL.getState(), 1)));
        rootElement.addElement(createUpdateFull(UPDATE_FULL.getNewValue(), tableName, parameterType, introspectedTable, pkColumn));

        // 删除给定主键的记录
        rootElement.addElement(new TextElement(xmlAnnotation(DELETE_BY_PK.getState(), 1)));
        rootElement.addElement(createDels(DELETE_BY_PK.getNewValue(), tableName, pkColumn));

        // 主键查找一条记录
        rootElement.addElement(new TextElement(xmlAnnotation(SELECT_BY_PK.getState(), 1)));
        rootElement.addElement(createSelect(SELECT_BY_PK.getNewValue(), tableName, parameterType, resultMap, pkColumn));

        // 查询列表
        rootElement.addElement(new TextElement(xmlAnnotation(SELECT_LIST.getState(), 1)));
        rootElement.addElement(createSelect(SELECT_LIST.getNewValue(), tableName, parameterType, resultMap, pkColumn));

        // 分页查询
        rootElement.addElement(new TextElement(xmlAnnotation(SELECT_PAGE.getState(), 1)));
        rootElement.addElement(createPageSelect(tableName, parameterType, resultMap, introspectedTable));

        return true;
    }


    /**
     * 注釋
     */
    private String xmlAnnotation(String content, int newlineCount, String... str) {
        StringBuilder newline = new StringBuilder();
        for (int i = 0; i < newlineCount; i++) {
            newline.append("\n");
        }

        return newline + (str.length >= 1 ? str[0] : "") + "<!-- " + content + " -->" + (str.length >= 2 ? str[1] : "");
    }

    /**
     * 通用查询sql
     */
    private String bulidWhereQueryList(IntrospectedTable introspectedTable, boolean format) {
        StringBuilder whereList = new StringBuilder();
        int size = introspectedTable.getAllColumns().size();
        int count = 0;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            count++;
            String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            String javaProperty = introspectedColumn.getJavaProperty();
            if (!introspectedColumn.isAutoIncrement()) {
                whereList.append("<if test=\"").append(javaProperty).append(" != null and !&quot;&quot;.equals(").append(javaProperty).append(")\">");
                if (format) {
                    if ("TIMESTAMP".equalsIgnoreCase(introspectedColumn.getJdbcTypeName())) {
                        whereList.append("and DATE_FORMAT(").append(columnName).append(",'%Y-%m-%d') = STR_TO_DATE(#{").append(javaProperty).append("},'%Y-%m-%d')</if>").append(size != count ? "\n" : "");
                    } else if ("VARCHAR".equalsIgnoreCase(introspectedColumn.getJdbcTypeName())) {
                        whereList.append("and ").append(columnName).append(" like concat('%',#{").append(javaProperty).append("},'%')</if>").append(size != count ? "\n" : "");
                    } else {
                        whereList.append("and ").append(columnName).append(" = #{").append(javaProperty).append("}</if>").append(size != count ? "\n" : "");
                    }
                } else {
                    whereList.append("and ").append(columnName).append(" = #{").append(javaProperty).append("}</if>").append(size != count ? "\n" : "");
                }
                whereList.append(FOUR_BLANK);
            }
        }
        return whereList.toString();
    }


    /**
     * 保存完整记录
     */
    private XmlElement createSaveFull(String id, String tableName, String parameterType, IntrospectedTable introspectedTable, IntrospectedColumn pkColumn) {
        XmlElement save = new XmlElement("insert");
        save.addAttribute(new Attribute("id", id));

        StringBuilder saveColumn = new StringBuilder("(");
        StringBuilder saveValue = new StringBuilder("(");
        int size = introspectedTable.getAllColumns().size();
        int count = 0;
        int columnLength;
        int valueLength;
        int columnCount = 0;
        int valueCount = 0;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            columnLength = saveColumn.length() - (100 * columnCount);
            valueLength = saveValue.length() - (100 * valueCount);
            count++;
            String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            String javaProperty = introspectedColumn.getJavaProperty();
            if (!introspectedColumn.isAutoIncrement()) {
                saveColumn.append(columnName).append(size != count ? ", " : "").append(columnLength > 100 ? "\n" + FOUR_BLANK + FOUR_BLANK : "");
                saveValue.append("#{").append(javaProperty).append("}").append(size != count ? ", " : "").append(valueLength > 100 ? "\n" + FOUR_BLANK + FOUR_BLANK : "");
                if (columnLength > 100) {
                    columnCount++;
                }
                if (valueLength > 100) {
                    valueCount++;
                }
            }
        }
        saveColumn.append(")");
        saveValue.append(")");

        if (null != pkColumn) {
            save.addAttribute(new Attribute("useGeneratedKeys", "true"));
            save.addAttribute(new Attribute("keyProperty", pkColumn.getJavaProperty()));
        }
        save.addAttribute(new Attribute("parameterType", parameterType));
        save.addElement(new TextElement("insert into " + tableName + " " + saveColumn.toString() +
                "\n" + FOUR_BLANK + "values " + saveValue.toString()));
        return save;
    }

    /**
     * 保存
     */
    private XmlElement createSave(String id, String tableName, String parameterType, IntrospectedTable introspectedTable, IntrospectedColumn pkColumn) {
        XmlElement save = new XmlElement("insert");
        save.addAttribute(new Attribute("id", id));
        String saveColumn = getSaveColumn(introspectedTable);
        String saveValue = getSaveValue(introspectedTable);
        if (null != pkColumn) {
            save.addAttribute(new Attribute("useGeneratedKeys", "true"));
            save.addAttribute(new Attribute("keyProperty", pkColumn.getJavaProperty()));
            save.addAttribute(new Attribute("parameterType", parameterType));
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
     * insert into 字段
     */
    private String getSaveColumn(IntrospectedTable introspectedTable) {
        StringBuilder saveColumn = new StringBuilder("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            String javaProperty = introspectedColumn.getJavaProperty();
            if (!introspectedColumn.isAutoIncrement()) {
                saveColumn.append("\t\t\t<if test=\"null != ").
                        append(javaProperty).append("\">").append(columnName).append(", </if>\n");
            }
        }
        return saveColumn.append("\t\t</trim>\n").toString();
    }

    /**
     * values 字段
     */
    private String getSaveValue(IntrospectedTable introspectedTable) {
        StringBuilder saveValue = new StringBuilder("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">\n");
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            String javaProperty = introspectedColumn.getJavaProperty();
            if (!introspectedColumn.isAutoIncrement()) {
                saveValue.append("\t\t\t<if test=\"null != ").append(javaProperty).append("\">").append("#{").append(javaProperty)
                        .append("}, </if>\n");
            }
        }
        return saveValue.append("\t\t</trim>").toString();
    }

    /**
     * 更新
     */
    private XmlElement createUpdate(String id, String tableName, String parameterType, IntrospectedTable introspectedTable, IntrospectedColumn pkColumn) {
        XmlElement update = new XmlElement("update");
        update.addAttribute(new Attribute("id", id));
        update.addAttribute(new Attribute("parameterType", parameterType));
        StringBuilder updateColumn = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            String javaProperty = introspectedColumn.getJavaProperty();
            // 拼接SQL
            if (!introspectedColumn.isAutoIncrement()) {
                updateColumn.append("\t\t\t<if test=\"null != ").append(javaProperty).append("\">");
                updateColumn.append(columnName).append(" = #{").append(javaProperty).append("}").append(", </if>\n");
            }
        }
        if ("update".equals(id)) {
            StringBuffer updateSQL = new StringBuffer("update ").append(tableName);
            updateSQL.append(" \n\t\t<set>\n").append(updateColumn.toString()).append("\t\t</set>");
            updateSQL.append("\n\t\twhere ").append(pkColumn.getActualColumnName()).append(" = ")
                    .append("#{").append(pkColumn.getJavaProperty()).append("}");
            update.addElement(new TextElement(updateSQL.toString()));
        }
        return update;
    }

    /**
     * 更新
     */
    private XmlElement createUpdateFull(String id, String tableName, String parameterType, IntrospectedTable introspectedTable, IntrospectedColumn pkColumn) {
        XmlElement update = new XmlElement("update");
        update.addAttribute(new Attribute("id", id));
        update.addAttribute(new Attribute("parameterType", parameterType));
        StringBuilder updateColumn = new StringBuilder();
        int size = introspectedTable.getAllColumns().size();
        int count = 0;
        for (IntrospectedColumn introspectedColumn : introspectedTable.getAllColumns()) {
            count++;
            String columnName = MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            String javaProperty = introspectedColumn.getJavaProperty();
            // 拼接SQL
            if (!introspectedColumn.isAutoIncrement()) {
                updateColumn.append(columnName).append(" = #{").append(javaProperty).append("}").append(size != count ? ", \n" + FOUR_BLANK + FOUR_BLANK + FOUR_BLANK : "");
            }
        }
        StringBuffer updateSQL = new StringBuffer("update ").append(tableName);
        updateSQL.append(" \n\t\tset ").append(updateColumn.toString()).append("\t");
        updateSQL.append("\n\t\twhere ").append(pkColumn.getActualColumnName()).append(" = ")
                .append("#{").append(pkColumn.getJavaProperty()).append("}");
        update.addElement(new TextElement(updateSQL.toString()));
        return update;
    }

    /**
     * 删除
     */
    private XmlElement createDels(String method, String tableName, IntrospectedColumn pkColumn) {
        XmlElement delete = new XmlElement("delete");
        delete.addAttribute(new Attribute("id", method));
        StringBuilder deleteStr = new StringBuilder("delete\n\tfrom ").append(tableName).append("\n\twhere").append(" ");
        deleteStr.append(pkColumn.getActualColumnName()).append(" = ").append("#{" + pkColumn.getJavaProperty() + "}");
        delete.addElement(new TextElement(deleteStr.toString()));
        return delete;
    }

    /**
     * 查询
     */
    private XmlElement createSelect(String id, String tableName, String parameterType, String resultMap, IntrospectedColumn pkColumn) {
        String baseColumnList = "<include refid=\"" + BASE_COLUMN_LIST.getValue() + "\" />";
        String baseWhereSQL = "<include refid=\"" + BASE_WHERE_SQL.getValue() + "\" />";
        XmlElement select = new XmlElement("select");
        select.addAttribute(new Attribute("id", id));
        StringBuilder selectStr = new StringBuilder();
        if ("count".equals(id)) {
            select.addAttribute(new Attribute("parameterType", parameterType));
            select.addAttribute(new Attribute("resultType", "int"));
            selectStr.append("select\n\t\t").append("count(*)").append("\n\t").append("from ").append(tableName);
            selectStr.append("\n\t\t").append(baseColumnList);
            select.addElement(new TextElement(selectStr.toString()));
        } else if ("exsit".equals(id)) {
            select.addAttribute(new Attribute("parameterType", parameterType));
            select.addAttribute(new Attribute("resultType", "boolean"));
            selectStr.append("select\n\t\t").append("count(*)").append("\n\t").append("from ").append(tableName);
            selectStr.append("\n\t\t").append(baseColumnList);
            select.addElement(new TextElement(selectStr.toString()));
        } else {
            select.addAttribute(new Attribute("resultMap", resultMap));
            selectStr.append("select\n\t\t").append(baseColumnList).append("\n\t").append("from ").append(tableName);
            if ("findByPk".equals(id)) {
                selectStr.append("\n\t").append("where ").append(pkColumn.getActualColumnName()).append(" = ").append("#{").append(pkColumn.getJavaProperty()).append("}");
                select.addElement(new TextElement(selectStr.toString()));
            }
            if ("findAll".equals(id)) {
                select.addElement(new TextElement(selectStr.toString()));
            }
            if ("findList".equals(id)) {
                select.addAttribute(new Attribute("parameterType", parameterType));
                selectStr.append("\n\t").append("where").append("\n\t\t").append(baseWhereSQL);
                select.addElement(new TextElement(selectStr.toString()));
            }
        }
        return select;
    }


    /**
     * 分页查询
     */
    private XmlElement createPageSelect(String tableName, String parameterType, String resultMap, IntrospectedTable introspectedTable) {
        XmlElement select = new XmlElement("select");
        select.addAttribute(new Attribute("id", "findPage"));
        select.addAttribute(new Attribute("resultMap", resultMap));
        /*select.addAttribute(new Attribute("parameterType", "cn.hejinyo.utils.PageQuery"));*/
        StringBuilder selectStr = new StringBuilder();
        selectStr.append("select\n\t\t").append("<include refid=\"Base_Column_List\" />").append("\n\t").append("from ").append(tableName);
        selectStr.append("\n\t")
                .append("<where>").append("\n\t")
                .append(bulidWhereQueryList(introspectedTable, true))
                .append("\n\t").append("</where>");
        select.addElement(new TextElement(selectStr.toString()));
        return select;
    }


}

