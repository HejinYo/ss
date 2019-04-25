package cn.hejinyo.ss.auth.security.filter;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 获取不需要认证就可以访问的路径
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/9/2 18:07
 */
@Data
@Component
@ConditionalOnExpression("!'${ss.auth.filter}'.isEmpty()")
@ConfigurationProperties(prefix = "ss.auth.filter")
public class SsCustomFilterConfig {
    private Set<String> clazz = new HashSet<>();
}
