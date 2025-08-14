package com.solution.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
                                new AntPathRequestMatcher("/api/user/signup"),
                                new AntPathRequestMatcher("/h2-console/**")
                        ).permitAll()
                        // 그 외 모든 요청은 인증된 사용자만 접근 가능
                        .anyRequest().authenticated()
                )

                // 폼 기반 로그인 설정
                .formLogin((formLogin) -> formLogin
                        .loginPage("/api/user/login") // 로그인 페이지 (실제로는 로그인 처리 API)
                        .defaultSuccessUrl("/")      // 로그인 성공 시 리다이렉트 경로
                        .permitAll()
                )
                // 로그아웃 설정
                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/api/user/logout"))
                        .logoutSuccessUrl("/")
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
