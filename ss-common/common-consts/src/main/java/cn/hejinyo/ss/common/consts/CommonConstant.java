package cn.hejinyo.ss.common.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
     * 通用状态
     */
    @Getter
    @AllArgsConstructor
    public enum Status {

        /**
         * 禁用
         */
        DISABLE(0),

        /**
         * 有效
         */
        VALID(1),
        ;

        private Integer value;

        public boolean equals(Integer value) {
            return this.value.equals(value);
        }
    }

}

