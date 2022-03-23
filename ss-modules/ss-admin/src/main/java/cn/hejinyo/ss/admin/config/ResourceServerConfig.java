/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.hejinyo.ss.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;

/**
 * @author Joe Grandja
 * @since 0.0.1
 */
@EnableWebSecurity
public class ResourceServerConfig {

    public class CustomerAuthenticationToken extends AbstractAuthenticationToken {

        private final String name;

        private Object principal;

        private Object credentials;

        public CustomerAuthenticationToken(Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
            super(authorities);
            this.setAuthenticated(true);
            this.name = jwt.getSubject();
        }

        @Override
        public Object getPrincipal() {
            return this.principal;
        }

        @Override
        public Object getCredentials() {
            return this.credentials;
        }
    }

    class CustomAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
        @Override
        public AbstractAuthenticationToken convert(Jwt jwt) {
//            Set<String> authoritiesSet = new HashSet<>();
//            authoritiesSet.add("ROLE_root");
//            authoritiesSet.add("sys:user:create");
            Collection<? extends GrantedAuthority> authorities = jwt.getClaim("scope");
//            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(authoritiesSet.toArray(new String[0]));
            return new CustomerAuthenticationToken(jwt, authorities);
        }
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("scope");
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri("http://127.0.0.1:9001/oauth2/jwks").build();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.mvcMatcher("/messages/**")
                .authorizeRequests()
                // 测试路径
                .antMatchers("/test/**").permitAll()
                .mvcMatchers("/messages/**").access("hasRole('ROLE_admin')")
//                .mvcMatchers("/messages/**").authenticated()
                .and()
                .oauth2ResourceServer(
                ).jwt();
        // 禁用 session, 很重要，否则网关过来的请求都可以直接访问了
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

}
