package cn.hejinyo.ss.auth.constant;

import cn.hejinyo.ss.common.core.enums.BaseEnum;
import com.fasterxml.jackson.annotation.JsonValue;
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
public enum TokenAudienceEnum implements BaseEnum<String> {

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

    @JsonValue
    private final String value;
    private final String desc;

}
