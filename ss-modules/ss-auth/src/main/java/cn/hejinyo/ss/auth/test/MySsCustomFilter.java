package cn.hejinyo.ss.auth.test;

import cn.hejinyo.ss.auth.security.filter.SsCustomFilter;
import cn.hejinyo.ss.auth.security.filter.SsUrlFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/04/25 11:46
 */
@Component
public class MySsCustomFilter {

    @Bean("ssCustomFilter")
    public SsCustomFilter ssCustomFilter() {
        SsCustomFilter filter = new SsCustomFilter();
        filter.addFilter("url",new SsUrlFilter());
        return filter;
    }
}
