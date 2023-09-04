package com.gora.backend.service.security;

import com.gora.backend.model.entity.UserEntity;
import com.gora.backend.repository.UserRepository;
import com.gora.backend.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if(StringUtils.isBlank(email)){
            throw new UsernameNotFoundException(email);
        }
        
        List<SimpleGrantedAuthority> grantedAuthorityList = new ArrayList<>();
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        
        userRoleRepository.findAllByUser(user).forEach(userRoleEntity -> {
            grantedAuthorityList.addAll(
                userRoleEntity.getRole().getRolePrivilegeEntityEntities().stream().map(privilegeEntity -> {
                    return new SimpleGrantedAuthority(privilegeEntity.getPrivilege().getCode());
                }).toList()
            );
        });
        

        return new User(user.getEmail(), String.valueOf(user.getSeq()), grantedAuthorityList);      
    }
}
