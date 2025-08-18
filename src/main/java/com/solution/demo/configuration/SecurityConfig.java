package com.solution.demo.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (Stateless REST API 환경에 적합)
                .csrf((csrf) -> csrf.disable())

                // H2 콘솔을 위한 헤더 설정 (개발 단계에서만 사용 권장)
                .headers((headers) -> headers
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))

                // HTTP 요청에 대한 접근 제어 설정
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        // 아래 경로들은 인증 없이 접근 허용
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/appuser/signup"),
                                new AntPathRequestMatcher("/api/appuser/login"), // 로그인 경로 허용
                                new AntPathRequestMatcher("/h2-console/**")
                        ).permitAll()
                        // 그 외 모든 요청은 인증된 사용자만 접근 가능
                        .anyRequest().authenticated()
                )

                // 폼 기반 로그인 설정을 REST API에 맞게 재구성
                .formLogin((formLogin) -> formLogin
                        .loginProcessingUrl("/api/appuser/login") // 로그인 요청을 처리할 URL
                        .usernameParameter("email") // 로그인 시 사용할 아이디 파라미터 이름 (기본값: username)
                        .successHandler((request, response, authentication) -> { // 로그인 성공 시 JSON 응답
                            response.setStatus(HttpStatus.OK.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            Map<String, String> body = Map.of("message", "로그인에 성공했습니다.");
                            new ObjectMapper().writeValue(response.getWriter(), body);
                        })
                        .failureHandler((request, response, exception) -> { // 로그인 실패 시 JSON 응답
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            Map<String, String> body = Map.of("message", "로그인에 실패했습니다: " + exception.getMessage());
                            new ObjectMapper().writeValue(response.getWriter(), body);
                        })
                        .permitAll()
                )
                // 로그아웃 설정
                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/api/user/logout"))
                        .logoutSuccessHandler((request, response, authentication) -> { // 로그아웃 성공 시 JSON 응답
                            response.setStatus(HttpStatus.OK.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            Map<String, String> body = Map.of("message", "로그아웃 되었습니다.");
                            new ObjectMapper().writeValue(response.getWriter(), body);
                        })
                        .invalidateHttpSession(true) // 세션 무효화
                );
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
