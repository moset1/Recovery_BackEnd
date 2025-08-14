package com.solution.demo.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solution.demo.dto.request.UserSignupRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("인증 없이 회원가입 API 접근 가능")
    void signup_endpoint_is_public() throws Exception {
        // given
        UserSignupRequestDto requestDto = new UserSignupRequestDto();
        requestDto.setName("testuser");
        requestDto.setEmail("public@example.com");
        requestDto.setPassword("password123");

        // when & then
        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().is(not(302))); // 302 Found (리다이렉트)가 아니면 성공
    }

    @Test
    @DisplayName("인증 없이 보호된 API 접근 시 로그인 페이지로 리다이렉트")
    void protected_endpoint_redirects_to_login() throws Exception {
        // given
        String protectedUrl = "/api/performance/daily?email=test@test.com&date=2026-01-01";

        // when & then
        mockMvc.perform(get(protectedUrl))
                .andDo(print())
                .andExpect(status().isFound()) // 302 Found 상태를 기대
                .andExpect(redirectedUrl("http://localhost/api/user/login")); // 로그인 페이지로 리다이렉트되는지 확인
    }
}