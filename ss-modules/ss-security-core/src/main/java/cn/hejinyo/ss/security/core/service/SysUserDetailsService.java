package cn.hejinyo.ss.security.core.service;

import cn.hejinyo.ss.security.core.dao.SysUserDao;
import cn.hejinyo.ss.security.core.model.SysUserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author : HejinYo   hejinyo@gmail.com
 * @date :  2019/4/19 23:22
 */
@Slf4j
@Component
public class SysUserDetailsService implements UserDetailsService {

    @Autowired
    private SysUserDao sysUserDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Locates the user based on the username. In the actual implementation, the search
     * may possibly be case sensitive, or case insensitive depending on how the
     * implementation instance is configured. In this case, the <code>UserDetails</code>
     * object that comes back may have a username that is of a different case than what
     * was actually requested..
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("登录用户名：{}", username);
        SysUserEntity userEntity = sysUserDao.findByUserName(username);
        log.info("userEntity：{}", userEntity);
        log.info("passwordEncoder：{}", passwordEncoder.encode(userEntity.getUserPwd()));
        return new User(username, userEntity.getUserPwd(),true,true,true,true, AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }
}
