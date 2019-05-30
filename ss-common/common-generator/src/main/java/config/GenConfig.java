package config;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * https://blog.csdn.net/zsq520520/article/details/50952830
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/5/10 22:41
 */
public class GenConfig {
    public static final String TEMPLATE_PATH = "ss-common/common-generator/src/main/java/ftl";
    public static final String FILE_PATH = "ss-common/common-generator/src/main/java";
    public static final String DTO_PACKAGE_PATH = "cn.hejinyo.ss.entity";
    public static final String PACKAGE_PATH = "cn.hejinyo.ss";
    private static final String TABLE_NAME = "sys_user";
    private static final String DOMAIN_OBJECT_NAME = "_Entity";
    private static final String MAPPER_NAME = "_Dao";

    public static void main(String[] args) throws InvalidConfigurationException, InterruptedException, SQLException, IOException {
        List<String> warnings = new ArrayList<>();
        // 覆盖已有文件
        boolean overwrite = true;
        // 代码生成器的配置
        Configuration config = new Configuration();

        // 初始化代码生成器的上下文环境
        Context context = new Context(ModelType.FLAT);
        context.setId("hejinyo_mybatis_generator");
        context.setTargetRuntime("MyBatis3");
        context.addProperty("javaFileEncoding", "UTF-8");
        // beginningDelimiter和endingDelimiter：指明数据库的用于标记数据库对象名的符号
        context.addProperty("beginningDelimiter", "`");
        context.addProperty("endingDelimiter", "`");
        // 格式化java代码
        context.addProperty("javaFormatter", "org.mybatis.generator.api.dom.DefaultJavaFormatter");
        // 格式化XML代码
        context.addProperty("xmlFormatter", "org.mybatis.generator.api.dom.DefaultXmlFormatter");

        // 通过自定义插件类生成自定义注解和接口
        PluginConfiguration pluginConfiguration = new PluginConfiguration();
        pluginConfiguration.setConfigurationType("org.mybatis.generator.plugins.SerializablePlugin");
        context.addPluginConfiguration(pluginConfiguration);

        // 自定义插件
        PluginConfiguration genPlugin = new PluginConfiguration();
        genPlugin.setConfigurationType(GenPlugin.class.getName());
        context.addPluginConfiguration(genPlugin);

        // 取消生成注释
        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        commentGeneratorConfiguration.addProperty("suppressAllComments", "true");
        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);

        // JDBC 的配置
        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setDriverClass("com.mysql.jdbc.Driver");
        jdbcConnectionConfiguration.setConnectionURL("jdbc:mysql://m.hejinyo.cn:3306/ss_jelly?characterEncoding=utf8&useSSL=false");
        jdbcConnectionConfiguration.setUserId("root");
        jdbcConnectionConfiguration.setPassword("Redhat@2018");
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);

        // 自定义java类型处理器
        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        javaTypeResolverConfiguration.setConfigurationType(MyJavaTypeResolver.class.getName());
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);

        /* 生成 entity 类 */
        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetProject(FILE_PATH);
        javaModelGeneratorConfiguration.setTargetPackage(PACKAGE_PATH + ".entity");
        // 不生成子包
        javaModelGeneratorConfiguration.addProperty("enableSubPackages", "true");
        // 设置是否在getter方法中，对String类型字段调用trim()方法
        javaModelGeneratorConfiguration.addProperty("trimStrings", "false");
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);


        // 生成 xml 文件
        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetProject(FILE_PATH);
        sqlMapGeneratorConfiguration.setTargetPackage(PACKAGE_PATH + ".mapper");
        // 不生成子包
        sqlMapGeneratorConfiguration.addProperty("enableSubPackages", "true");
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);


        // 生成 mapper 接口
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        javaClientGeneratorConfiguration.setTargetProject(FILE_PATH);
        javaClientGeneratorConfiguration.setTargetPackage(PACKAGE_PATH + ".mapper");
        // 不生成子包
        sqlMapGeneratorConfiguration.addProperty("enableSubPackages", "true");
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);


        // 设置需要生成代码的表名称
        TableConfiguration tableConfiguration = new TableConfiguration(context);
        tableConfiguration.setTableName(TABLE_NAME);
        tableConfiguration.setDomainObjectName(UnderlineToHump(tableConfiguration.getTableName() + DOMAIN_OBJECT_NAME));
        tableConfiguration.setMapperName(UnderlineToHump(tableConfiguration.getTableName() + MAPPER_NAME));
        tableConfiguration.setInsertStatementEnabled(true);
        tableConfiguration.setSelectByPrimaryKeyStatementEnabled(true);
        tableConfiguration.setDeleteByPrimaryKeyStatementEnabled(true);
        tableConfiguration.setUpdateByPrimaryKeyStatementEnabled(true);
        tableConfiguration.setCountByExampleStatementEnabled(false);
        tableConfiguration.setUpdateByExampleStatementEnabled(false);
        tableConfiguration.setDeleteByExampleStatementEnabled(false);
        tableConfiguration.setSelectByExampleStatementEnabled(false);
        context.addTableConfiguration(tableConfiguration);

        // 输出上下文环境的内容
        String xml = context.toXmlElement().getFormattedContent(1);
        // System.out.println(xml);

        // 将上下文环境添加到代码生成器配置中
        config.addContext(context);

        // 生成代码
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }


    /**
     * 下划线转首字母大写驼峰
     */
    public static String UnderlineToHump(String para) {
        StringBuilder result = new StringBuilder();
        String[] a = para.split("_");
        for (String s : a) {
            if (!para.contains("_")) {
                result.append(s);
                continue;
            }
            if (result.length() == 0) {
                result.append(s.substring(0, 1).toUpperCase());
                result.append(s.substring(1).toLowerCase());
            } else {
                result.append(s.substring(0, 1).toUpperCase());
                result.append(s.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }
}
