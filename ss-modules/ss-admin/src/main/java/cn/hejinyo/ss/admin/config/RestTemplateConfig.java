package cn.hejinyo.ss.admin.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/11/20 22:01
 */
@Configuration
public class RestTemplateConfig {
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int CONNECTION_MANAGER_CONNECTION_REQUEST_TIMEOUT = 0;
    private static final int SOCKET_TIMEOUT = 5000;
    private static final int MAX_TOTAL = 1000;
    private static final int MAX_PER_ROUTE = 32;

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

        connectionManager.setMaxTotal(MAX_TOTAL);
        connectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
        connectionManager.setValidateAfterInactivity(CONNECT_TIMEOUT);
        return connectionManager;
    }

    @Bean
    public RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_MANAGER_CONNECTION_REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();
    }

    public static class CustomRequestRetryHandler extends DefaultHttpRequestRetryHandler {
        /*protected static final Collection<Class<? extends IOException>> ignoredExceptions =
                Arrays.asList(UnknownHostException.class, SSLException.class);*/

        protected static final Collection<Class<? extends IOException>> IGNORED_EXCEPTIONS =
                Arrays.asList(ConnectTimeoutException.class, UnknownHostException.class);

        public CustomRequestRetryHandler(final int retryCount) {
            super(retryCount, true, IGNORED_EXCEPTIONS);
        }
    }

    @Bean
    public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager, RequestConfig requestConfig) {
        return HttpClientBuilder
                .create()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(new CustomRequestRetryHandler(5))
                .build();
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient(poolingHttpClientConnectionManager(), requestConfig()));

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }
}
