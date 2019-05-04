package cn.hejinyo.ss.jelly.consts;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/1/29 23:12
 */
public class Constant {
    /**
     * 数据权限过滤
     */
    public static final String SQL_FILTER = "sqlFilter";
    /**
     * 超级管理员ID
     */
    public static final Integer SUPER_ADMIN = 1;
    /**
     * 树根节点ID
     */
    public static final Integer TREE_ROOT = 1;

    /**
     * server_jwt_sub
     */
    public static final String JWT_SERVER = "clam-server";

    /**
     * 服务内部接口前缀
     */
    public static final String SERVER_API = "/ss-server/api/";
    public static final String SERVER_API_ANON = SERVER_API + "**";

    /**
     * jwtToken 超时时间 小时
     */
    public static final int JWT_EXPIRES_DEFAULT = 12;

    /**
     * 用户登录token的续命时长 秒
     */
    public static final int USER_TOKEN_EXPIRE = 1800;

    /**
     * 用户权限的有效时长 秒
     */
    public static final int USER_PERM_EXPIRE = 600;


    public enum Dept {
        /**
         * 所在部门
         */
        CUR_DEPT("curDept"),
        /**
         * 子部门
         */
        SUB_DEPT("subDept"),
        /**
         * 所有部门
         */
        ALL_DEPT("allDept");

        private String value;

        Dept(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum AuthoritiesEnum {
        /**
         * 超级管理员角色
         */
        ADMIN("ROLE_ADMIN"),
        /**
         * 基本角色
         */
        USER("ROLE_USER"),
        /**
         * 匿名角色
         */
        ANONYMOUS("ROLE_ANONYMOUS");

        private String role;

        AuthoritiesEnum(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }

    }

    /**
     * 菜单类型
     */
    public enum MenuType {
        /**
         * 目录
         */
        CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private Integer value;

        MenuType(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        public boolean equals(Integer value) {
            return this.getValue().equals(value);
        }
    }

    /**
     * 通用状态
     */
    public enum Status {
        /**
         * 正常
         */
        NORMAL(0),
        /**
         * 禁用
         */
        DISABLE(1),
        /**
         * 锁定
         */
        LOCK(2);

        private Integer value;

        Status(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        public boolean equals(Integer value) {
            return value != null && value.equals(this.value);
        }
    }

    /**
     * 数据类型
     * 0：字符串 1：整型  2：浮点型  3：布尔  4：json对象
     */
    public enum DataType {
        /**
         * 字符串
         */
        STRING(0),
        /**
         * 整型
         */
        INTEGER(1),
        /**
         * 浮点型
         */
        DOUBLE(2),
        /**
         * 布尔
         */
        BOOLEAN(3),
        /**
         * json对象
         */
        JSON(4);

        private Integer value;

        DataType(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

        public boolean equals(Integer value) {
            return value.equals(this.value);
        }
    }


    /**
     * 云服务商
     */
    public enum CloudService {
        /**
         * 阿里云
         */
        ALIYUN(0),
        /**
         * 腾讯云
         */
        QCLOUD(1),
        /**
         * 七牛云
         */
        QINIU(2);

        private int value;

        CloudService(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}

