package cn.hejinyo.ss.auth.server.api;

import cn.hejinyo.ss.auth.server.dto.AuthCheckResult;
import cn.hejinyo.ss.auth.server.service.JellyAuthApiService;
import cn.hejinyo.ss.auth.server.service.JellyService;
import cn.hejinyo.ss.common.utils.MicroserviceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微服务api接口
 *
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/04/27 00:58
 */
@RestController
@Api(tags = "JellyAuth微服务接口")
public class JellyAuthApi implements JellyAuthApiService {

    @Autowired
    private JellyService jellyService;

    /**
     * 校验用户token，成功则返回角色，权限
     */
    @Override
    @ApiOperation(value = "校验用户token", notes = "成功则返回角色，权限")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", name = "userId", value = "用户编号", required = true, dataType = "int"),
            @ApiImplicitParam(paramType = "path", name = "jti", value = "token编号", required = true, dataType = "String"),
    })
    public MicroserviceResult<AuthCheckResult> checkToken(Integer userId, String jti) {
        return MicroserviceResult.ok(jellyService.checkToken(userId, jti));
    }
}
