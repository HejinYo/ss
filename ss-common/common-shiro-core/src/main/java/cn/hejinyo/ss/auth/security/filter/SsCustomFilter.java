package cn.hejinyo.ss.auth.security.filter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/04/25 11:06
 */
@Getter
@Slf4j
public class SsCustomFilter {

    private static final String AUTH = "auth";

    private Map<String, Filter> filterList = new HashMap<>();

    @PostConstruct
    public void postConstruct() throws Exception {
        this.filterList.put(AUTH, authFilter());
    }

    public void addFilter(String name, Filter filter) {
        if (!AUTH.equals(name)) {
            this.filterList.put(name, filter);
        } else {
            log.error("{}拦截器已经存在，已经忽略本次添加", name);
        }
    }


    /**
     * 基本认证拦截器
     */
    @Bean
    public SsAuthcFilter authFilter() {
        return new SsAuthcFilter();
    }

    /**
     * 解决自定义拦截器混乱问题
     */
    @Bean
    @SuppressWarnings("unchecked")
    public FilterRegistrationBean registrationAuthcFilterBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean(authFilter());
        //取消自动注册功能 Filter自动注册,不会添加到FilterChain中.
        registration.setEnabled(false);
        return registration;
    }

}
