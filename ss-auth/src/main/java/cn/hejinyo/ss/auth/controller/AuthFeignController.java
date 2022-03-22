package cn.hejinyo.ss.auth.controller;

import cn.hejinyo.ss.auth.util.RedisKeys;
import cn.hejinyo.ss.auth.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/12/20 20:34
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ms/auth/")
public class AuthFeignController {

    private final RedisUtils redisUtils;

    /**
     * 通过访问token获取业务token
     *
     * @param accessToken 访问token
     * @return String
     */
    @PostMapping("/getMsToken")
    public String getMsToken(@RequestBody String accessToken) {
        String token = redisUtils.get(RedisKeys.USER_TOKEN + accessToken);
        if (StringUtils.hasText(token)) {
            return token;
        }
        // token过期
        return null;
    }

}
