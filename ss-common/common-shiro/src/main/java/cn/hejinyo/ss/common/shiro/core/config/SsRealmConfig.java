package cn.hejinyo.ss.common.shiro.core.config;

import cn.hejinyo.ss.common.shiro.core.override.ShiroModularRealm;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.realm.Realm;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义Realm注册到shiro
 *
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/04/25 11:06
 */
@Getter
@Slf4j
public class SsRealmConfig {

    private List<Realm> realms = new ArrayList<>();

    /**
     * 添加realm
     */
    public void addRealm(Realm realm) {
        this.realms.add(realm);
    }

    /**
     * 自定义多Reams配置
     */
    public ShiroModularRealm shiroModularRealm() {
        ShiroModularRealm modularRealm = new ShiroModularRealm();
        // 注入所有自定义Reams
        modularRealm.setRealms(this.realms);
        return modularRealm;
    }

}
