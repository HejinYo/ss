package cn.hejinyo.ss.common.shiro.core.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置不需要拦截的uri路径和指定路径的拦截器
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/9/2 18:07
 */
@Data
@Component
@ConditionalOnExpression("!'${ss.auth.filter}'.isEmpty()")
@ConfigurationProperties(prefix = "ss.auth.filter")
public class SsAuthFilterProperties {
    /**
     * 不拦截路径
     */
    private List<String> anonPath = new ArrayList<>();

    /**
     * 自定义拦截器和路径
     */
    private Map<String,String> filterChainMap = new HashMap<>();
}
