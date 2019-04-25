package cn.hejinyo.ss.auth.token;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.shiro.authc.AuthenticationToken;

import java.util.Set;

/**
 * 用于shiro验证的TOKEN,非用户token,
 * 主体为用户名，凭证为jwt
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2018/9/2 15:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SsAuthToken extends SsUserDetails implements AuthenticationToken {

    public SsAuthToken(String userName, Integer userId, String jwt, Set<String> roleSet, Set<String> permSet) {
        super.setUserName(userName);
        super.setUserId(userId);
        super.setJwt(jwt);
        super.setRoleSet(roleSet);
        super.setPermSet(permSet);
    }

    @Override
    public Object getPrincipal() {
        return getUserName();
    }

    @Override
    public Object getCredentials() {
        return getJwt();
    }
}
