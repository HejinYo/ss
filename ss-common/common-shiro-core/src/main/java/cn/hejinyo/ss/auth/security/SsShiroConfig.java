package cn.hejinyo.ss.auth.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.*;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/8/17 23:22
 */
@Configuration
@Slf4j
public class SsShiroConfig {

    /**
     * SecurityManager 安全管理器 有多个Realm,可使用'realms'属性代替
     */
    @Bean("securityManager")
    protected SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 禁用session 的subjectFactory
        securityManager.setSubjectFactory(new ShiroSubjectFactory());
        // 禁用使用 Sessions 作为存储策略的实现，但它没有完全地禁用Sessions,所以需要配合 context. setSessionCreationEnabled(false)
        ((DefaultSessionStorageEvaluator) ((DefaultSubjectDAO) securityManager.getSubjectDAO())
                .getSessionStorageEvaluator()).setSessionStorageEnabled(false);
        // 自定义realms
        securityManager.setRealms(getRealms());
        // 自定义 ModularRealm
        securityManager.setAuthenticator(getAuthenticator());
        log.error("SecurityManager 安全管理器 有多个Realm,可使用'realms'属性代替");
        return securityManager;
    }

    /**
     * 自定义realms
     */
    private List<Realm> getRealms() {
        // 自定义realms
        List<Realm> realms = new ArrayList<>();
        realms.add(ssAuthRealm());
        return realms;
    }

    /**
     * token验证Realm
     */
    @Bean
    public SsAuthRealm ssAuthRealm() {
        return new SsAuthRealm();
    }

    /**
     * 自定义 ModularRealm
     */
    private ShiroModularRealm getAuthenticator() {
        ShiroModularRealm authenticator = new ShiroModularRealm();
        authenticator.setRealms(getRealms());
        return authenticator;
    }

    /**
     * 配置使用自定义认证器，可以实现多Realm认证，并且可以指定特定Realm处理特定类型的验证
     */
    @Bean
    public ShiroModularRealm defaultModularRealm(List<Realm> realms) {
        ShiroModularRealm authenticator = new ShiroModularRealm();
        authenticator.setRealms(realms);
        return authenticator;
    }

    @Bean("shiroFilter")
    protected ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        //自定义访问验证拦截器
        factoryBean.setFilters(getFilters());
        // 拦截器链
        factoryBean.setFilterChainDefinitionMap(getFilterChainDefinitionMap());
        return factoryBean;
    }


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

    /**
     * 注入自定义拦截器,注意拦截器自注入问题
     */
    private Map<String, Filter> getFilters() {
        Map<String, Filter> list = new HashMap<>();
        list.put("auth", authFilter());
        list.put("url", new SsUrlFilter());
        return list;
    }

    @Bean
    public SsFilterAnonConfig filterAnonConfig() {
        return new SsFilterAnonConfig();
    }

    /**
     * 拦截器链
     */
    private Map<String, String> getFilterChainDefinitionMap() {
        // 拦截器链
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterAnonConfig().getPath().forEach(path -> {
            log.info("不拦截路径 => {}", path);
            filterMap.put(path, "anon");
        });
        filterMap.put("/**", "url,auth");
        return filterMap;
    }

    /**
     * 在方法中 注入  securityManager ，进行代理控制,相当于调用SecurityUtils.setSecurityManager(securityManager)
     */
    @Bean
    protected MethodInvokingFactoryBean getMethodInvokingFactoryBean() {
        MethodInvokingFactoryBean factoryBean = new MethodInvokingFactoryBean();
        factoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        factoryBean.setArguments(securityManager());
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
