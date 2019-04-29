package cn.hejinyo.ss.auth.jelly.config;

import cn.hejinyo.ss.auth.jelly.feign.JellyAuthService;
import cn.hejinyo.ss.auth.jelly.filter.SsAuthcFilter;
import cn.hejinyo.ss.auth.jelly.filter.SsUrlFilter;
import cn.hejinyo.ss.auth.jelly.realm.SsAuthRealm;
import cn.hejinyo.ss.common.shiro.core.config.SsFilterConfig;
import cn.hejinyo.ss.common.shiro.core.config.SsRealmConfig;
import cn.hejinyo.ss.common.shiro.core.properties.SsAuthFilterProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/25 22:49
 */
@Component
public class AuthShiroConfig {

    public static final String AUTH_FILTER = "auth";

    public static final String URL_FILTER = "url";

    /**
     * 基本认证拦截器
     */
    @Bean
    public SsAuthcFilter authFilter(JellyAuthService jellyAuthService) {
        return new SsAuthcFilter(jellyAuthService);
    }

    @Bean
    public SsFilterConfig ssCustomFilter(SsAuthFilterProperties ssAuthFilterProperties, JellyAuthService jellyAuthService) {
        SsFilterConfig filter = new SsFilterConfig();
        filter.addFilter(AUTH_FILTER, authFilter(jellyAuthService));
        filter.addFilter(URL_FILTER, new SsUrlFilter());
        // 最后全部拦截
        ssAuthFilterProperties.getFilterChainMap().add("/**;" + AUTH_FILTER);
        return filter;
    }

    /**
     * 解决自定义拦截器混乱问题
     */
    @Bean
    @SuppressWarnings("unchecked")
    public FilterRegistrationBean registrationAuthcFilterBean(JellyAuthService jellyAuthService) {
        FilterRegistrationBean registration = new FilterRegistrationBean(authFilter(jellyAuthService));
        //取消自动注册功能 Filter自动注册,不会添加到FilterChain中.
        registration.setEnabled(false);
        return registration;
    }

    /**
     * 主要的认证权限 SsAuthRealm
     */
    @Bean
    public SsRealmConfig ssRealmConfig() {
        SsRealmConfig ssRealmConfig = new SsRealmConfig();
        ssRealmConfig.addRealm(new SsAuthRealm());
        return ssRealmConfig;
    }

}
