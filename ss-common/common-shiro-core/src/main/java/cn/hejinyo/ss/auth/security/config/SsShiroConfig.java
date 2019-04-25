package cn.hejinyo.ss.auth.security.config;

import cn.hejinyo.ss.auth.security.filter.SsCustomFilter;
import cn.hejinyo.ss.auth.security.realm.SsReamsConfig;
import cn.hejinyo.ss.auth.security.shiro.ShiroSubjectFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/17 23:22
 */
@Configuration
@Slf4j
public class SsShiroConfig {

    @Bean
    @ConditionalOnMissingBean(SsCustomFilter.class)
    public SsCustomFilter ssCustomFilter() {
        return new SsCustomFilter();
    }

    /**
     * SecurityManager 安全管理器 有多个Realm,可使用'realms'属性代替
     */
    @Bean("securityManager")
    protected SecurityManager securityManager(SsReamsConfig ssReamsConfig) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 禁用session 的subjectFactory
        securityManager.setSubjectFactory(new ShiroSubjectFactory());
        // 禁用使用 Sessions 作为存储策略的实现，但它没有完全地禁用Sessions,所以需要配合 context. setSessionCreationEnabled(false)
        ((DefaultSessionStorageEvaluator) ((DefaultSubjectDAO) securityManager.getSubjectDAO())
                .getSessionStorageEvaluator()).setSessionStorageEnabled(false);

        // 自定义realms
        securityManager.setRealms(ssReamsConfig.realms());
        log.info("自定义realms====> {}", ssReamsConfig.realms().stream().map(v -> v.getClass().getName()).collect(Collectors.toList()));
        // 自定义 ModularRealm
        securityManager.setAuthenticator(ssReamsConfig.shiroModularRealm());
        return securityManager;
    }


    @Bean("shiroFilter")
    protected ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager, SsFilterAnonConfig filterAnonConfig, SsCustomFilter ssCustomFilter) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);

        //自定义访问验证拦截器
        factoryBean.setFilters(ssCustomFilter.getFilterList());
        log.info("ssCustomFilter====> {}", ssCustomFilter.getFilterList());

        // 拦截器链
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterAnonConfig.getPath().forEach(path -> {
            log.info("不拦截路径 => {}", path);
            filterMap.put(path, "anon");
        });
        filterAnonConfig.getFilterChainMap().forEach(m -> {
            String[] map = m.split(",");
            log.info("getFilterChainMap => {},{}", map[0], map[1]);
            filterMap.put(map[0], map[1]);
        });
        filterMap.put("/**", "auth");
        factoryBean.setFilterChainDefinitionMap(filterMap);

        log.info("ss-shiro-shiroFilter 初始化完成");
        return factoryBean;
    }


    /**
     * 在方法中 注入  securityManager ，进行代理控制,相当于调用SecurityUtils.setSecurityManager(securityManager)
     */
    @Bean
    protected MethodInvokingFactoryBean getMethodInvokingFactoryBean(SsReamsConfig ssReamsConfig) {
        MethodInvokingFactoryBean factoryBean = new MethodInvokingFactoryBean();
        factoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        factoryBean.setArguments(securityManager(ssReamsConfig));
        return factoryBean;
    }

    /**
     * Shiro生命周期处理器
     */
    @Bean("lifecycleBeanPostProcessor")
    protected LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启Shiro的注解(如@RequiresRoles,@RequiresPermissions),需借助SpringAOP扫描使用Shiro注解的类,并在必要时进行安全逻辑验证
     * 配置以下两个bean(DefaultAdvisorAutoProxyCreator(可选)和AuthorizationAttributeSourceAdvisor)即可实现此功能
     */
    @Bean
    @DependsOn({"lifecycleBeanPostProcessor"})
    protected DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    /**
     * 注解RequiresPermissions 需要此配置，否侧注解不生效，和上面aop搭配才有效,这里会出问题，导致controller失效，还没有解决方案
     */
    @Bean
    protected AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }


}
