package com.gora.backend.service.security;

import com.gora.backend.model.entity.UserEntity;
import com.gora.backend.repository.UserRepository;
import com.gora.backend.repository.UserRoleCustomRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;

import java.util.List;

@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserRoleCustomRepository userRoleCustomRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if(StringUtils.isBlank(email)){
            throw new UsernameNotFoundException(email);
        }

        UserEntity user = userRepository.findByEmailAndDisable(email,false).orElseThrow(() -> new UsernameNotFoundException(email));

        List<SimpleGrantedAuthority> grantedAuthorityList = userRoleCustomRepository.findUserPrivilege(user).stream()
                .map(SimpleGrantedAuthority::new).toList();

        return new User(user.getEmail(), String.valueOf(user.getSeq()), grantedAuthorityList);      
    }
}
