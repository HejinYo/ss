package cn.hejinyo.ss.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * security 规范的用户查询实现
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2021/10/31 20:30
 */
@RequiredArgsConstructor
@Service("userDetailsService")
public class UserDetailServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        // TODO 查询RPC接口
        if (!"HejinYo".equals(userName)) {
            throw new UsernameNotFoundException("用户[" + userName + "]不存在");
        }
        Set<String> authoritiesSet = new HashSet<>();
        authoritiesSet.add("ROLE_root");
        authoritiesSet.add("sys:user:create");
        Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(authoritiesSet.toArray(new String[0]));
        boolean enabled = StringUtils.hasLength(userName);
        return new SsAuthUser("HejinYo", new BCryptPasswordEncoder().encode("123456"), enabled, enabled, enabled, enabled, authorities);
    }
}
