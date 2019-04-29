package cn.hejinyo.ss.auth.server.api;

import cn.hejinyo.ss.auth.server.dto.AuthCheckResult;
import cn.hejinyo.ss.auth.server.service.JellyAuthApiService;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashSet;

/**
 * 微服务api接口
 *
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/04/27 00:58
 */
@RestController
public class JellyAuthApi implements JellyAuthApiService {

    /**
     * 校验用户token，成功则返回角色，权限
     */
    @Override
    public AuthCheckResult checkToken(Integer userId, String jti) {
        AuthCheckResult authCheckResult = new AuthCheckResult();
        authCheckResult.setPass(true);
        authCheckResult.setRoleSet(new HashSet<>(Arrays.asList("admin", "class")));
        return authCheckResult;
    }
}
