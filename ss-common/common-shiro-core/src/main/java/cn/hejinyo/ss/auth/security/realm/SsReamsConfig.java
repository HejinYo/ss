package cn.hejinyo.ss.auth.security.realm;

import cn.hejinyo.ss.auth.security.shiro.ShiroModularRealm;
import lombok.Data;
import org.apache.shiro.realm.Realm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 自定义Reams相关配置
 *
 * @author : anthony.s.he   hejinyo@gmail.com
 * @date : 2019/04/25 10:12
 */
@Component("ssReamsConfig")
public class SsReamsConfig {
    @Autowired
    private SsCustomReamsConfig ssCustomReamsConfig;

    private List<Realm> customRealms = new ArrayList<>();
    private SsAuthRealm authRealm = new SsAuthRealm();

    @PostConstruct
    public void postConstruct() throws Exception {
        this.customRealms.add(authRealm);
        Set<String> clazz = ssCustomReamsConfig.getClazz();
        for (String c : clazz) {
            if (!authRealm.getClass().getName().equals(c)) {
                Class realm = Class.forName(c);
                Object obj = realm.newInstance();
                this.customRealms.add((Realm) obj);
            }
        }
    }

    /**
     * 主要的认证权限 SsAuthRealm
     */
    public SsAuthRealm ssAuthRealm() {
        return this.authRealm;
    }

    /**
     * 所有的Realm
     */
    public List<Realm> realms() {
        return this.customRealms;
    }

    /**
     * 自定义多Reams配置
     */
    public ShiroModularRealm shiroModularRealm() {
        ShiroModularRealm modularRealm = new ShiroModularRealm();
        // 注入所有自定义Reams
        modularRealm.setRealms(this.customRealms);
        return modularRealm;
    }

}
