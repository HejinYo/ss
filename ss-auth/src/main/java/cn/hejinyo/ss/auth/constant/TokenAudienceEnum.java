package cn.hejinyo.ss.auth.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 签发的token用于登陆的受众
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/3/28 20:32
 */
@Getter
@AllArgsConstructor
public enum TokenAudienceEnum {

    /**
     * 网页端
     */
    SS_WEB("ss-web", "网页"),

    /**
     * APP
     */
    SS_MOBILE("ss-app", "APP"),

    /**
     * 微信公众号
     */
    SS_WECHAT_OFFIACCOUNT("ss-wechat-offiaccount", "微信公众号"),

    /**
     * ss微信公众号
     */
    SS_WECHAT_MINIPROGRAM("ss-wechat-miniprogram", "微信小程序");

    private String value;
    private String desc;
}
