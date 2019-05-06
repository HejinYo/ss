package cn.hejinyo.ss.common.shiro.core.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义拦截器注册到shiro
 *
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/04/25 11:06
 */
@Getter
@Slf4j
public class SsFilterConfig {

    private Map<String, Filter> filterList = new HashMap<>();

    /**
     * 添加拦截器
     */
    public void addFilter(String name, Filter filter) {
        this.filterList.put(name, filter);
    }


}
