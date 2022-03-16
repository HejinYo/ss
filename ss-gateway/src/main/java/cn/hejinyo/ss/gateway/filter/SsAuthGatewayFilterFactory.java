package cn.hejinyo.ss.gateway.filter;

import cn.hejinyo.ss.gateway.feign.AuthService;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Ss-Auth 授权过滤器
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/17 22:25
 */
@Component
public class SsAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<SsAuthGatewayFilterFactory.Config> {

    private final AuthService authService;

    @Lazy
    public SsAuthGatewayFilterFactory(AuthService authService) {
        super(Config.class);
        this.authService = authService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        // grab configuration from Config object
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            HttpHeaders httpHeaders = request.getHeaders();
            String accessToken = httpHeaders.getFirst("Authorization");

            // 异步调用，否则会报错
            CompletableFuture<String> f = CompletableFuture.supplyAsync(()-> authService.getMsToken(accessToken));
            try {
                String msToken = f.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            // 获取访问token, 请求 Ss-auth 服务获得微服务token
            Mono<Principal> mono = exchange.getPrincipal();

            // 微服务token放入到request header中

            // If you want to build a "pre" filter you need to manipulate the
            // request before calling chain.filter
            ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
            builder.header("Authorization", accessToken);
            // use builder to manipulate the request
            return chain.filter(exchange.mutate().request(builder.build()).build());
        };
    }

    public static class Config {
        // Put the configuration properties for your filter here
    }
}
