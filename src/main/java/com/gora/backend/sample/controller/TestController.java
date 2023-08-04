package com.gora.backend.sample.controller;

import com.gora.backend.model.entity.user.UserEntity;
import com.gora.backend.repository.UserRepository;
import com.gora.backend.sample.querydsl.RoleCustomRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.Map;

@Controller
public class TestController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleCustomRepository roleCustomRepository;
    @GetMapping("/user")
    @ResponseBody
    @Transactional
    public Map<String, Object> user(@AuthenticationPrincipal
                                    String principal) {

        userRepository.save(UserEntity.builder().email("wqewqe").build());
        roleCustomRepository.save();
//        throw new RuntimeException();
        return Collections.singletonMap("name", roleCustomRepository.get());
    }
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

    @Autowired
    private ModelMapper modelMapper;
    @GetMapping("/b")
    @ResponseBody
    public void indewwqex() {
        // when similar source object is provided
//        Game game = new Game(1L, "Game 1");
//        GameDTO gameDTO = modelMapper.map(game, GameDTO.class);
//        roleCustomRepository.get();
        System.out.printf("wqewq");
    }

    @GetMapping("/a")
    @ResponseBody
    public String index() {
        return "aa";
    }

//    @GetMapping("/oauth2/callback/google")
//    @ResponseBody
//    public void callback(@RequestParam String code){
//        System.out.printf("test");
//    }
    @GetMapping("/")
    @ResponseBody
    public String indewqeex() {
        return "aaqewwqeqw";
    }
}
