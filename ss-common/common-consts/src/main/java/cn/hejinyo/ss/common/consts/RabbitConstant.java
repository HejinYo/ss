package cn.hejinyo.ss.common.consts;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/1/29 23:12
 * MQ 消息队列
 */
public class RabbitConstant {

    public static interface calmLog {
        /**
         * 交换机
         */
        String EXCHANGE = "CALM_LOG_EXCHANGE";
        /**
         * 路由
         */
        String KEY = "CALM_LOG_KEY";
        /**
         * log rabbit队列名称
         */
        String QUEUE = "CALM_LOG_QUEUE";
    }


}
