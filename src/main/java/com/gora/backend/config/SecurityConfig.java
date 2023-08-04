package com.gora.backend.config;

import com.gora.backend.constant.EnvironmentKey;
import com.gora.backend.constant.FrontUrl;
import com.gora.backend.filter.JwtTokenAuthenticationFilter;
import com.gora.backend.handler.LoginSuccessHandler;
import com.gora.backend.model.eIgnoreSecurityPath;
import com.gora.backend.repository.TokenRepository;
import com.gora.backend.repository.UserRepository;
import com.gora.backend.repository.UserRoleCustomRepository;
import com.gora.backend.service.security.*;
import com.gora.backend.util.token.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        final String loginPageUrl = environment.getProperty(EnvironmentKey.APP_FRONT_URL) + FrontUrl.LOGIN;
        http
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
//                todo 이거 빼고도 다운로드 되는지 확인 필요
//                .cors().configurationSource(corsConfigurationSource())
//                .and()
                .authorizeHttpRequests()
                .requestMatchers(getAntRequestMatchers()).permitAll()
                .anyRequest().authenticated()
                .and()
                .logout()
//               적용되는지 확인필요
                .logoutUrl("/a")
//                todo 리다이렉션 안됨 핸들러 들어오기는함
//                .logoutSuccessHandler((request, response, authentication) -> {
//                    response.sendRedirect("/");
//                    request.cooki
//                    System.out.printf("logout");
//                })
                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize")
                .and()
                .loginPage(loginPageUrl)
                .successHandler((request, response, authentication) -> {
                    response.sendRedirect("/b");
                    System.out.printf("success");
                })
                .failureHandler((request, response, exception) -> {
                    System.out.printf("fail");
                })
                .userInfoEndpoint()
                .userService(oauth2UserService());

//        http
//                .sessionManagement()
//                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .cors().configurationSource(corsConfigurationSource())
//                .and()
//                .httpBasic().disable()
//                .authorizeHttpRequests()
//                .requestMatchers(getAntRequestMatchers())
//                    .permitAll()
//                .anyRequest()
//                    .authenticated()
//                .and()
//                .oauth2Login()
//                    .loginPage(loginPageUrl)
//                    .successHandler(new AuthenticationSuccessHandlerImpl(loginSuccessHandler))
//                    .defaultSuccessUrl("/b")
//                    .failureHandler(new AuthenticationFailHandlerImpl())
//                    .failureUrl("/a")
//                    .authorizationEndpoint()
//                        .baseUri("/oauth2/authorize")
//                        .authorizationRequestRepository(cookieAuthorizationRequestRepository())
//                todo application.yml 파일에서 셋팅해야 적용됨 여기서는 안됨
//                    .and()
//                    .redirectionEndpoint()
//                        .baseUri("/oauth2/callback/**")
//                .and()
//                    .userInfoEndpoint()
//                        .userService(oauth2UserService())
        ;
//        todo 추가시 인증안된 세션 login 페이지 리다이렉션 안함
//                http.addFilterBefore(new JwtTokenAuthenticationFilter(jwtTokenProvider(), eIgnoreSecurityPath.getAntRequestMatchers(), tokenUtils), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public Oauth2UserService oauth2UserService() {
        return new Oauth2UserService();
    }

//    private HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
//        return new HttpCookieOAuth2AuthorizationRequestRepository();
//    }

    @Bean
    public UserDetailsServiceImpl userDetailsService() {
        return new UserDetailsServiceImpl(userRepository, userRoleCustomRepository);
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(userDetailsService(),tokenRepository, tokenUtils);
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
