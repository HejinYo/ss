package cn.hejinyo.ss.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/11/2 23:06
 */
@Slf4j
@Component
public class AccessFilter implements GlobalFilter {

    private static final String START_TIME = "startTime";

    // nginx需要配置
    private static final String X_REAL_IP = "X-Real-IP";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String info = String.format("Method:{%s} Host:{%s} Path:{%s} Query:{%s}",
                Objects.requireNonNull(exchange.getRequest().getMethod()).name(), exchange.getRequest().getURI().getHost(),
                exchange.getRequest().getURI().getPath(), exchange.getRequest().getQueryParams());
        if (log.isDebugEnabled()) {
            log.info(info);
        }
        System.out.println("请求转发："+info);
        exchange.getAttributes().put(START_TIME, System.currentTimeMillis());
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute(START_TIME);
            if (startTime != null) {
                Long executeTime = (System.currentTimeMillis() - startTime);
                List<String> ips = exchange.getRequest().getHeaders().get(X_REAL_IP);
                String ip = ips != null ? ips.get(0) : null;
                String api = exchange.getRequest().getURI().getRawPath();
                int code = exchange.getResponse().getStatusCode() != null
                        ? exchange.getResponse().getStatusCode().value() : 500;
                // 当前仅记录日志，后续可以添加日志队列，来过滤请求慢的接口
                if (log.isDebugEnabled()) {
                    log.info("来自IP地址：{}的请求接口：{}，响应状态码：{}，请求耗时：{}ms", ip, api, code, executeTime);
                }
            }
        }));
    }
}
