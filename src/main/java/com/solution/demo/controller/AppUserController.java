package com.solution.demo.controller;

import com.solution.demo.dto.request.LoginRequestDto;
import com.solution.demo.framework.security.JwtTokenProvider;
import com.solution.demo.service.AppUserService;
import com.solution.demo.user.UserCreateForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/appuser")
public class AppUserController {

    private final AppUserService appUserService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@Valid @RequestBody UserCreateForm userCreateForm) {
        appUserService.create(userCreateForm.getName(),
                userCreateForm.getEmail(), userCreateForm.getPassword());

        // 회원가입 성공 후 바로 로그인 처리 및 토큰 발급
        String token = jwtTokenProvider.createToken(userCreateForm.getEmail(), Collections.singletonList("ROLE_USER"));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            // Spring Security를 통한 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // JWT 토큰 생성
            String token = jwtTokenProvider.createToken(userDetails.getUsername(), userDetails.getAuthorities().stream().map(Object::toString).toList());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (BadCredentialsException e) {
            // 인증 실패
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "아이디 또는 비밀번호가 맞지 않습니다."));
        }
    }

}
