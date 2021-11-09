//package cn.hejinyo.ss.auth.config;
//
//
//import cn.hejinyo.ss.auth.security.CustomTokenEnhancer;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.crypto.password.NoOpPasswordEncoder;
//import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
//import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
//import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
//import org.springframework.security.oauth2.provider.token.TokenEnhancer;
//import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
//import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import javax.sql.DataSource;
//import java.util.Arrays;
//
//@Configuration
//@RequiredArgsConstructor
//@EnableAuthorizationServer //开启授权服务器
//public class OAuth2ServerConfiguration2 extends AuthorizationServerConfigurerAdapter {
//
//    private final DataSource dataSource;
//
//    private final AuthenticationManager authenticationManager;
//
//    private final RedisConnectionFactory redisConnectionFactory;
//
//    /**
//     * 我们配置了使用数据库来维护客户端信息。虽然在各种Demo中我们经常看到的是在内存中维护客户端信息，通过配置直接写死在这里。
//     * 但是，对于实际的应用我们一般都会用数据库来维护这个信息，甚至还会建立一套工作流来允许客户端自己申请ClientID，实现OAuth客户端接入的审批。
//     *
//     * @param clients
//     * @throws Exception
//     */
//    @Override
//    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        clients.jdbc(dataSource);
//    }
//
//    /**
//     * 这里干了两件事儿。首先，打开了验证Token的访问权限（以便之后我们演示）。
//     * 然后，允许ClientSecret明文方式保存，并且可以通过表单提交（而不仅仅是Basic Auth方式提交），之后会演示到这个。
//     *
//     * @param security
//     * @throws Exception
//     */
//    @Override
//    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
//        security.checkTokenAccess("permitAll()")
//                .allowFormAuthenticationForClients().passwordEncoder(NoOpPasswordEncoder.getInstance());
//    }
//
//    /**
//     * 干了以下4件事儿：
//     * 1. 配置我们的令牌存放方式为JWT方式，而不是内存、数据库或Redis方式。
//     * JWT是Json Web Token的缩写，也就是使用JSON数据格式包装的令牌，由.号把整个JWT分隔为头、数据体、签名三部分。
//     * JWT保存Token虽然易于使用但是不是那么安全，一般用于内部，且需要走HTTPS并配置比较短的失效时间。
//     * 2. 配置JWT Token的非对称加密来进行签名
//     * 3. 配置一个自定义的Token增强器，把更多信息放入Token中
//     * 4. 配置使用JDBC数据库方式来保存用户的授权批准记录
//     *
//     * @param endpoints
//     * @throws Exception
//     */
//    @Override
//    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
//        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
//        tokenEnhancerChain.setTokenEnhancers(
//                Arrays.asList(tokenEnhancer()));
//
//        endpoints.approvalStore(approvalStore())
//                .authorizationCodeServices(authorizationCodeServices())
//                .tokenStore(tokenStore())
//                .tokenEnhancer(tokenEnhancerChain)
//                .authenticationManager(authenticationManager);
//    }
//
//    /**
//     * 使用JDBC数据库方式来保存授权码
//     *
//     * @return
//     */
//    @Bean
//    public AuthorizationCodeServices authorizationCodeServices() {
//        return new JdbcAuthorizationCodeServices(dataSource);
//    }
//
//    @Bean
//    public TokenStore tokenStore() {
//        RedisTokenStore tokenStore = new RedisTokenStore(redisConnectionFactory);
//        tokenStore.setPrefix("ss:auth:token:");
//        return tokenStore;
//    }
//
//    /**
//     * 使用JDBC数据库方式来保存用户的授权批准记录
//     *
//     * @return
//     */
//    @Bean
//    public JdbcApprovalStore approvalStore() {
//        return new JdbcApprovalStore(dataSource);
//    }
//
//    /**
//     * 自定义的Token增强器，把更多信息放入Token中
//     *
//     * @return
//     */
//    @Bean
//    public TokenEnhancer tokenEnhancer() {
//        return new CustomTokenEnhancer();
//    }
//
//    /**
//     * 配置登录页面的视图信息（其实可以独立一个配置类，这样会更规范）
//     */
//    @Configuration
//    static class MvcConfig implements WebMvcConfigurer {
//        @Override
//        public void addViewControllers(ViewControllerRegistry registry) {
//            registry.addViewController("login").setViewName("login");
//        }
//    }
//}
