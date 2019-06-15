package cn.hejinyo.ss.jelly.consts;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/1/29 23:12
 */
public class JellyConstant {

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

