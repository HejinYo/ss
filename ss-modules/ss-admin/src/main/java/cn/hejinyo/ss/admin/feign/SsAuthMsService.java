package cn.hejinyo.ss.admin.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/20 20:34
 */
@FeignClient(contextId = "ssAuthMsService", value = "ss-auth")
public interface SsAuthMsService {

    /**
     * 获取 jwkSet
     *
     * @return String
     */
    @GetMapping("/ms/auth/jwkSet")
    String jwkSet();

}
