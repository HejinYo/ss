package cn.hejinyo.ss.common.consts;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/1/29 23:12
 */
public class CommonConstant {

    /**
     * jelly 服务前缀
     */
    public static final String JELLY_AUTH = "jelly";

    /**
     * 微服务内部接口前缀
     */
    public static final String MICRO_SERVER_API = "/ss-micro-service/api";

    /**
     * 超级管理员ID
     */
    public static final Integer SUPER_ADMIN = 1;
    /**
     * 树根节点ID
     */
    public static final Integer TREE_ROOT = 1;

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

}

