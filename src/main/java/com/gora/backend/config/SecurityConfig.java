package com.gora.backend.config;

import static com.gora.backend.model.eIgnoreSecurityPath.getAntRequestMatchers;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gora.backend.common.EnvironmentKey;
import com.gora.backend.common.FrontUrl;
import com.gora.backend.common.token.TokenUtils;
import com.gora.backend.filter.ExceptionHandlerFilter;
import com.gora.backend.filter.JwtTokenAuthenticationFilter;
import com.gora.backend.handler.LoginSuccessHandler;
import com.gora.backend.model.eIgnoreSecurityPath;
import com.gora.backend.repository.TokenRepository;
import com.gora.backend.repository.UserRepository;
import com.gora.backend.repository.UserRoleRepository;
import com.gora.backend.service.security.AuthenticationFailHandlerImpl;
import com.gora.backend.service.security.AuthenticationSuccessHandlerImpl;
import com.gora.backend.service.security.JwtTokenProvider;
import com.gora.backend.service.security.LogoutHandlerImpl;
import com.gora.backend.service.security.LogoutSuccessHandlerImpl;
import com.gora.backend.service.security.Oauth2UserService;
import com.gora.backend.service.security.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final LoginSuccessHandler loginSuccessHandler;
    private final TokenRepository tokenRepository;
    private final Environment environment;
    private final UserRepository userRepository;
    private final TokenUtils tokenUtils;
    private final LogoutSuccessHandlerImpl logoutSuccessHandler;
    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;
    private final UserRoleRepository userRoleRepository;
    private final LogoutHandlerImpl logoutHandlerImpl;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        final String loginPageUrl = environment.getProperty(EnvironmentKey.APP_FRONT_URL) + FrontUrl.LOGIN;
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> headers.frameOptions().disable())
                .authorizeHttpRequests()
                .requestMatchers(getAntRequestMatchers()).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .logout(logout -> logout
                        .addLogoutHandler(logoutHandlerImpl)
                        .logoutUrl("/api/v1/logout")
                        .logoutSuccessHandler(logoutSuccessHandler))
                .oauth2Login(login -> login
                        .authorizationEndpoint()
                        // oauth2Login만 하면 필요없는데 추가설정 들어가니까 이 url 없으면 안됨
                        .baseUri("/oauth2/authorize")
                        .and()
                        .loginPage(loginPageUrl)
                        .successHandler(new AuthenticationSuccessHandlerImpl(loginSuccessHandler))
                        .failureHandler(new AuthenticationFailHandlerImpl())
                        .userInfoEndpoint()
                        .userService(oauth2UserService()));

        // 필터 순서 중요 에러 필터는 에러 예상되는 필터보다 먼저 호출되어야한다.
        http.addFilterBefore(new ExceptionHandlerFilter(messageSource, objectMapper),
                UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JwtTokenAuthenticationFilter(jwtTokenProvider(),
                eIgnoreSecurityPath.getAntRequestMatchers(), tokenUtils), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    AuthenticationFailureHandler authenticationFailHandler() {
        return new AuthenticationFailHandlerImpl();
    }

    @Bean
    AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandlerImpl(loginSuccessHandler);
    }

    @Bean
    Oauth2UserService oauth2UserService() {
        return new Oauth2UserService();
    }

    @Bean
    UserDetailsServiceImpl UserDetailsServiceImpl() {
        return new UserDetailsServiceImpl(userRepository, userRoleRepository);
    }

    @Bean
    JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(UserDetailsServiceImpl(), tokenRepository, tokenUtils);
    }
}
