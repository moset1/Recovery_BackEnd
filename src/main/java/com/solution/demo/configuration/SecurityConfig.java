package com.solution.demo.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solution.demo.framework.security.JwtAuthenticationFilter;
import com.solution.demo.framework.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (Stateless REST API 환경에 적합)
                .csrf(AbstractHttpConfigurer::disable)
                
                // 세션을 사용하지 않도록 설정 (Stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

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
                // REST API에 맞는 예외 처리 설정
                .exceptionHandling(exceptions -> exceptions
                        // 인증되지 않은 사용자가 보호된 리소스에 접근 시
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            Map<String, String> body = Map.of("message", "인증이 필요합니다. 먼저 로그인 해주세요.");
                            objectMapper.writeValue(response.getWriter(), body);
                        })
                        // 인증은 되었으나 권한이 없는 리소스에 접근 시
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            Map<String, String> body = Map.of("message", "해당 리소스에 접근할 권한이 없습니다.");
                            objectMapper.writeValue(response.getWriter(), body);
                        })
                )
                // 직접 구현한 JWT 필터를 UsernamePasswordAuthenticationFilter 전에 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

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
