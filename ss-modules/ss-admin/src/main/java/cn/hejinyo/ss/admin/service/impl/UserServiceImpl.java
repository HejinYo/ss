package cn.hejinyo.ss.admin.service.impl;

import cn.hejinyo.ss.admin.entity.UserEntity;
import cn.hejinyo.ss.admin.repository.UserRepository;
import cn.hejinyo.ss.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户管理服务
 *
 * @author : HejinYo   hejinyo@gmail.com
 * @date : 2022/4/6 21:50
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public UserDetails getByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity != null) {
            boolean enabled = StringUtils.hasLength(username);
            Set<String> authoritiesSet = new HashSet<>();
            authoritiesSet.add("ROLE_admin");
            authoritiesSet.add("sys:user:create");
            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(authoritiesSet.toArray(new String[0]));
            // PasswordEncoderFactories.createDelegatingPasswordEncoder().encode("123456")
            return new User(username, userEntity.getPassword(), enabled, enabled, enabled, enabled, authorities);
        }
        return null;
    }
}
