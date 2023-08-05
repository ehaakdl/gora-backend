package com.gora.backend.sample.controller;

import com.gora.backend.model.entity.*;
import com.gora.backend.repository.*;
import com.gora.backend.sample.querydsl.RoleCustomRepository;
import jakarta.transaction.Transactional;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class TestController {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RoleCustomRepository roleCustomRepository;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Game {

        private Long id;
        private String name;
        private Long timestamp;

        public Game(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
        // constructors, getters and setters
    }
    @NoArgsConstructor
    @Getter
    @Setter
    public static class GameDTO {

        private Long id;
        private String name;

        public GameDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
        // constructors, getters and setters
    }

    @GetMapping("/user")
    @ResponseBody
    @Transactional
    public Map<String, Object> user(@AuthenticationPrincipal
                                    String principal) {


        return Collections.singletonMap("name", roleCustomRepository.get());
    }

    private final PrivilegeRepository privilegeRepository;
    private final RolePrivilegeRepository rolePrivilegeRepository;
    private final RoleRepository roleRepository;
    private final SocialUserRepository socialUserRepository;
    private final TokenRepository tokenRepository;
    private final UserRoleRepository userRoleRepository;

//    todo 자동으로 생성 수정 시간 값 셋팅 추가하기
    @GetMapping("/db")
    @ResponseBody
    @org.springframework.transaction.annotation.Transactional
    public void dbTest() {
        PrivilegeEntity privilegeEntity=privilegeRepository.save(PrivilegeEntity.builder()
                        .createdBy(1L)
                .updatedBy(1L)
                .code("ewqeq")

                        .displayName("eqwe")
                .build());
        RoleEntity roleEntity = roleRepository.save(RoleEntity.builder().createdBy(1L)
                .updatedBy(1L).code("qwewe").displayName("eqwewqe").build());
        rolePrivilegeRepository.save(RolePrivilegeEntity.builder()
                .createdBy(1L)
                .updatedBy(1L)
                .roleSeq(roleEntity.getSeq())
                        .privilegeSeq(privilegeEntity.getSeq())
                .build());

        socialUserRepository.save(SocialUserEntity.builder()
                .createdBy(1L)
                .updatedBy(1L)
                .socialType(eSocialType.google)
                .build());
        UserEntity userEntity = userRepository.save(UserEntity.builder()
                .createdBy(1L)
                .updatedBy(1L)
                        .email("eqweq")
                .type(eUserType.basic)
                .password("wqe")
                .build());
        tokenRepository.save(TokenEntity.createAccessToken(userEntity.getSeq(),"qwew", "qweqwe",new Date()));
        userRoleRepository.save(UserRoleEntity.builder()
                        .userSeq(userEntity.getSeq())
                        .roleSeq(roleEntity.getSeq())
                .build());

    }

    @GetMapping("/modelmapper")
    @ResponseBody
    public void modelmapper() {
        // when similar source object is provided
        Game game = new Game(1L, "Game 1");
        GameDTO gameDTO = modelMapper.map(game, GameDTO.class);
    }
}
