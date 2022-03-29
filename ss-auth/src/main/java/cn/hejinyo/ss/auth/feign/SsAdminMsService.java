package cn.hejinyo.ss.auth.feign;

import cn.hejinyo.ss.auth.vo.SsUserInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/20 20:34
 */
@FeignClient(contextId = "ssAdminMsService", value = "ss-admin")
public interface SsAdminMsService {

    /**
     * 通过访问token获取业务token
     *
     * @param userName String
     * @return String
     */
    @PostMapping("/ms/admin/user/loadUserByUsername")
    SsUserInfoVo loadUserByUsername(@RequestBody String userName);

}
