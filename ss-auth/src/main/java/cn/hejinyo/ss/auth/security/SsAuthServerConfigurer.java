package cn.hejinyo.ss.auth.security;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.web.NimbusJwkSetEndpointFilter;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * SsAuthServer服务配置
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/11/3 22:27
 */
public class SsAuthServerConfigurer<B extends HttpSecurityBuilder<B>> extends AbstractHttpConfigurer<SsAuthServerConfigurer<B>, B> {

    private RequestMatcher requestMatcher;
    private AuthenticationConverter accessTokenRequestConverter;
    private final List<AuthenticationProvider> authenticationProviders = new LinkedList<>();
    private AuthenticationSuccessHandler accessTokenResponseHandler;
    private AuthenticationFailureHandler errorResponseHandler;

    private RequestMatcher jwkSetEndpointMatcher;

    private void initEndpointMatchers(ProviderSettings providerSettings) {
        this.jwkSetEndpointMatcher = new AntPathRequestMatcher(providerSettings.getJwkSetEndpoint(), HttpMethod.GET.name());
    }

    @Override
    public void init(B builder) {
        ProviderSettings providerSettings = SsAuthServerUtils.getProviderSettings(builder);
        initEndpointMatchers(providerSettings);
        this.requestMatcher = new AntPathRequestMatcher(providerSettings.getTokenEndpoint(), HttpMethod.POST.name());

        List<AuthenticationProvider> authenticationProviders =
                !this.authenticationProviders.isEmpty() ?
                        this.authenticationProviders :
                        createDefaultAuthenticationProviders(builder);
        authenticationProviders.forEach(authenticationProvider ->
                builder.authenticationProvider(postProcess(authenticationProvider)));
    }

    @Override
    public void configure(B builder) {
        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
        ProviderSettings providerSettings = SsAuthServerUtils.getProviderSettings(builder);

        NimbusJwkSetEndpointFilter jwkSetEndpointFilter =
                new NimbusJwkSetEndpointFilter(
                        SsAuthServerUtils.getJwkSource(builder),
                        providerSettings.getJwkSetEndpoint());
        builder.addFilterBefore(postProcess(jwkSetEndpointFilter), AbstractPreAuthenticatedProcessingFilter.class);

        SsAuthLoginFilter tokenEndpointFilter =
                new SsAuthLoginFilter(authenticationManager, providerSettings.getTokenEndpoint());
        if (this.accessTokenResponseHandler != null) {
            tokenEndpointFilter.setAuthenticationSuccessHandler(this.accessTokenResponseHandler);
        }
        if (this.errorResponseHandler != null) {
            tokenEndpointFilter.setAuthenticationFailureHandler(this.errorResponseHandler);
        }
        if (this.accessTokenRequestConverter != null) {
            tokenEndpointFilter.setAuthenticationConverter(this.accessTokenRequestConverter);
        }

        builder.addFilterAfter(postProcess(tokenEndpointFilter), AbstractPreAuthenticatedProcessingFilter.class);


        ExceptionHandlingConfigurer<B> exceptionHandling = builder.getConfigurer(ExceptionHandlingConfigurer.class);
        if (exceptionHandling != null) {
            exceptionHandling.defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    new OrRequestMatcher(this.getRequestMatcher())
            );
        }
    }

    public RequestMatcher getRequestMatcher() {
        return (request) -> this.requestMatcher.matches(request) || this.jwkSetEndpointMatcher.matches(request);
    }

    private <B extends HttpSecurityBuilder<B>> List<AuthenticationProvider> createDefaultAuthenticationProviders(B builder) {
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
        JwtEncoder jwtEncoder = SsAuthServerUtils.getJwtEncoder(builder);
        OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer = SsAuthServerUtils.getJwtCustomizer(builder);
        UserDetailsService userDetailsService = SsAuthServerUtils.getBean(builder, UserDetailsService.class);
        PasswordEncoder passwordEncoder = SsAuthServerUtils.getBean(builder, PasswordEncoder.class);
        ProviderSettings providerSettings = SsAuthServerUtils.getProviderSettings(builder);
        RegisteredClientRepository registeredClientRepository = SsAuthServerUtils.getRegisteredClientRepository(builder);
        SsAuthLoginProvider ssAuthLoginProvider = new SsAuthLoginProvider(registeredClientRepository, userDetailsService, passwordEncoder, jwtEncoder, providerSettings);
        if (jwtCustomizer != null) {
            ssAuthLoginProvider.setJwtCustomizer(jwtCustomizer);
        }
        authenticationProviders.add(ssAuthLoginProvider);
        return authenticationProviders;
    }

    public void accessTokenRequestConverter(AuthenticationConverter accessTokenRequestConverter) {
        this.accessTokenRequestConverter = accessTokenRequestConverter;
    }

    public void authenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProviders.add(authenticationProvider);
    }

    public void accessTokenResponseHandler(AuthenticationSuccessHandler accessTokenResponseHandler) {
        this.accessTokenResponseHandler = accessTokenResponseHandler;
    }

    public void errorResponseHandler(AuthenticationFailureHandler errorResponseHandler) {
        this.errorResponseHandler = errorResponseHandler;
    }

}
