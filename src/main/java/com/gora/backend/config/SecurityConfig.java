package com.gora.backend.config;

import com.gora.backend.common.EnvironmentKey;
import com.gora.backend.common.FrontUrl;
import com.gora.backend.filter.JwtTokenAuthenticationFilter;
import com.gora.backend.handler.LoginSuccessHandler;
import com.gora.backend.model.eIgnoreSecurityPath;
import com.gora.backend.repository.TokenRepository;
import com.gora.backend.repository.UserRepository;
import com.gora.backend.repository.UserRoleCustomRepository;
import com.gora.backend.service.security.*;
import com.gora.backend.common.token.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static com.gora.backend.model.eIgnoreSecurityPath.getAntRequestMatchers;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRoleCustomRepository userRoleCustomRepository;
    private final LoginSuccessHandler loginSuccessHandler;
    private final TokenRepository tokenRepository;
    private final Environment environment;
    private final UserRepository userRepository;
    private final TokenUtils tokenUtils;
    private final LogoutSuccessHandlerImpl logoutSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        final String loginPageUrl = environment.getProperty(EnvironmentKey.APP_FRONT_URL) + FrontUrl.LOGIN;
        http
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
//                웹에서 다운로드 호출 시 cors 발생해서 필요
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .authorizeHttpRequests()
                .requestMatchers(getAntRequestMatchers()).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .logout()
                .logoutUrl("/api/v1/logout")
//                todo 리다이렉션 안됨 핸들러 들어오기는함
                .logoutSuccessHandler(logoutSuccessHandler)
                .and()
                .oauth2Login()
                .authorizationEndpoint()
//                oauth2Login만 하면 필요없는데 추가설정 들어가니까 이 url 없으면 안됨
                .baseUri("/oauth2/authorize")
                .and()
                .loginPage(loginPageUrl)
                .successHandler(new AuthenticationSuccessHandlerImpl(loginSuccessHandler))
                .failureHandler(new AuthenticationFailHandlerImpl())
                .userInfoEndpoint()
                .userService(oauth2UserService());

        //        todo 추가시 인증안된 세션 login 페이지 리다이렉션 안함
//        http.addFilterBefore(new JwtTokenAuthenticationFilter(jwtTokenProvider(), eIgnoreSecurityPath.getAntRequestMatchers(), tokenUtils), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public Oauth2UserService oauth2UserService() {
        return new Oauth2UserService();
    }

    @Bean
    public UserDetailsServiceImpl userDetailsService() {
        return new UserDetailsServiceImpl(userRepository, userRoleCustomRepository);
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(userDetailsService(), tokenRepository, tokenUtils);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
