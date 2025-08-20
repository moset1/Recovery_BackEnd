package com.solution.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solution.demo.service.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String testEmail = "testlogin@example.com";
    private final String testPassword = "password1234";

    @BeforeEach
    void setUp() {
        // 테스트 실행 전, 테스트용 사용자를 미리 생성
        try {
            appUserService.create("testloginuser", testEmail, testPassword);
        } catch (Exception e) {
            // 이미 사용자가 존재하면 무시 (다른 테스트에서 생성했을 경우)
        }
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception {
        Map<String, String> loginRequest = Map.of("email", testEmail, "password", testPassword);

        mockMvc.perform(post("/api/appuser/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                // 응답으로 'token' 필드가 존재하는지 확인
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_fail_wrong_password() throws Exception {
        Map<String, String> loginRequest = Map.of("email", testEmail, "password", "wrongpassword");

        mockMvc.perform(post("/api/appuser/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                // 로그인 실패 시 반환되는 메시지 확인 (실제 구현에 맞게 수정 필요)
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 맞지 않습니다."))
                .andDo(print());
    }
}