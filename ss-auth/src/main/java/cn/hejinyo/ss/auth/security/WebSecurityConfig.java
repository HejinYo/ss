package cn.hejinyo.ss.auth.security;

import cn.hejinyo.ss.auth.handler.SsAuthenticationFailureHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * WebSecurityConfig 配置类
 * 配置web访问
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/10/30 21:19
 */
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 配置用户账户的认证方式。显然，我们把用户存在了数据库中希望配置JDBC的方式。
     * 此外，我们还配置了使用BCryptPasswordEncoder哈希来保存用户的密码（生产环境中，用户密码肯定不能是明文保存的）
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }

    /**
     * 开放/login和/oauth/authorize两个路径的匿名访问。前者用于登录，后者用于换授权码，这两个端点访问的时机都在登录之前。
     * 设置/login使用表单验证进行登录。
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 登陆的页面和登陆成功跳转页面配置 .loginProcessingUrl()=登陆逻辑的处理路径，默认POST /login
        // .successForwardUrl()=成功转发 .defaultSuccessUrl(url,true)=成功重定向 .successHandler()=自定义成功逻辑处理
        // 登陆失败的路径需要放开无认证访问
        // .failureForwardUrl()=失败转发 .failureUrl()=失败重定向 .failureHandler()=失败的处理器
        // formLogin().usernameParameter() formLogin().passwordParameter() 重写username password参数名称
        http.formLogin().loginPage("/login").failureHandler(new SsAuthenticationFailureHandler("/login", true));

        // 配合权限校验，不认证/认证访问页面
        http.authorizeRequests()
                // 可以不认证访问的页面
                .antMatchers("/login", "/oauth/authorize","/css/**", "/img/**", "/test/**").permitAll()
                // 其他的都需要认证访问
                .anyRequest().authenticated();
        // 关闭 csrf
        http.csrf().disable();
    }
}