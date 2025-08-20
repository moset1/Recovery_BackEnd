package com.solution.demo.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solution.demo.user.UserCreateForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // 각 테스트 후 DB 상태를 롤백하여 테스트 격리 보장
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("인증 없이 회원가입 API 접근 가능하며 성공 시 201 Created 반환")
    void signup_endpoint_is_public_and_returns_created() throws Exception {
        // given
        UserCreateForm requestDto = new UserCreateForm();
        requestDto.setName("testuser");
        requestDto.setEmail("public@example.com");
        requestDto.setPassword("password123");

        // when & then
        mockMvc.perform(post("/api/appuser/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated()) // 201 Created 상태를 명확히 기대
                .andExpect(jsonPath("$.token").exists()); // 응답에 'token' 필드가 있는지 확인
    }

    @Test
    @DisplayName("인증 없이 보호된 API 접근 시 401 Unauthorized 반환")
    void protected_endpoint_returns_401_unauthorized() throws Exception {
        // given
        String protectedUrl = "/api/performance/daily?email=test@test.com&date=2026-01-01";

        // when & then
        mockMvc.perform(get(protectedUrl))
                .andDo(print())
                .andExpect(status().isUnauthorized()) // 401 Unauthorized 상태를 기대
                .andExpect(jsonPath("$.message").value("인증이 필요합니다. 먼저 로그인 해주세요."));
    }
}