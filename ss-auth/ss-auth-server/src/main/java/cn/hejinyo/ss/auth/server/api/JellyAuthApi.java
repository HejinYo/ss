package cn.hejinyo.ss.auth.server.api;

import cn.hejinyo.ss.auth.server.dto.AuthCheckResult;
import cn.hejinyo.ss.auth.server.service.JellyAuthApiService;
import cn.hejinyo.ss.auth.server.service.JellyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微服务api接口
 *
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/04/27 00:58
 */
@RestController
public class JellyAuthApi implements JellyAuthApiService {

    @Autowired
    private JellyService jellyService;

    /**
     * 校验用户token，成功则返回角色，权限
     */
    @Override
    public AuthCheckResult checkToken(Integer userId, String jti) {
        return jellyService.checkToken(userId, jti);
    }
}
