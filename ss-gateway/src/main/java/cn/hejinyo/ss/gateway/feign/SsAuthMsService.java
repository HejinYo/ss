package cn.hejinyo.ss.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/20 20:34
 */
@FeignClient(contextId = "ssAuthMsService", value = "ss-auth")
public interface SsAuthMsService {

    /**
     * 通过访问token获取业务token
     *
     * @param accessToken 访问token
     * @return String
     */
    @PostMapping("/ms/auth/checkAndGetMsToken")
    String checkAndGetMsToken(@RequestBody String accessToken);

}
