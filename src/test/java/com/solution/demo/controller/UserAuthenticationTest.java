package com.solution.demo.controller;

import com.solution.demo.service.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        mockMvc.perform(post("/api/appuser/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", testEmail)
                        .param("password", testPassword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그인에 성공했습니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_fail_wrong_password() throws Exception {
        mockMvc.perform(post("/api/appuser/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", testEmail)
                        .param("password", "wrongpassword"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("로그인에 실패했습니다: Bad credentials"))
                .andDo(print());
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_success() throws Exception {
        // 1. 먼저 로그인을 성공시켜 세션을 얻는다.
        MvcResult loginResult = mockMvc.perform(post("/api/appuser/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", testEmail)
                        .param("password", testPassword))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();
        assertNotNull(session);

        // 2. 얻은 세션을 사용하여 로그아웃을 요청한다.
        mockMvc.perform(post("/api/user/logout").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그아웃 되었습니다."))
                .andDo(print());
    }
}