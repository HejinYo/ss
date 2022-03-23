package cn.hejinyo.ss.gateway.filter;

import cn.hejinyo.ss.gateway.feign.AuthService;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

    private final static String SS = "ss ";

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
            // If you want to build a "pre" filter you need to manipulate the request before calling chain.filter
            ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
            // 微服务token放入到request header中
            builder.header("Authorization", this.getMsToken(httpHeaders.getFirst("Authorization")));
            // use builder to manipulate the request
            return chain.filter(exchange.mutate().request(builder.build()).build());
        };
    }

    /**
     * 检测并获取msToken
     *
     * @param accessToken String
     * @return String
     */
    private String getMsToken(String accessToken) {
        if (StringUtils.hasText(accessToken) && accessToken.startsWith(SS)) {
            // 异步调用，否则会报错
            CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> authService.getMsToken(accessToken.replace(SS, "")));
            try {
                return "Bearer " + f.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return accessToken;
    }

    public static class Config {
        // Put the configuration properties for your filter here
    }
}
