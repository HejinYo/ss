package cn.hejinyo.ss.auth.security.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 对特定的token指定realm进行处理
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2017/7/30 13:03
 */
public class ShiroModularRealm extends ModularRealmAuthenticator {

    @Override
    protected AuthenticationInfo doAuthenticate(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 判断getRealms()是否返回为空
        assertRealmsConfigured();
        // 所有Realm
        Collection<Realm> realms = getRealms();
        // 登录类型对应的所有Realm
        Collection<Realm> typeRealms = new ArrayList<>();
        // 强制转换Token
        for (Realm realm : realms) {
            //根据token类型指定realm
            if (realm.supports(authenticationToken)) {
                typeRealms.add(realm);
            }
        }

        // 判断是单Realm还是多Realm
        if (typeRealms.size() == 1) {
            return doSingleRealmAuthentication(typeRealms.iterator().next(), authenticationToken);
        } else {
            return doMultiRealmAuthentication(typeRealms, authenticationToken);
        }
    }
}
