package com.solution.demo.controller;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // 테스트 후 데이터 롤백
class AppUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() throws Exception {
        // given
        UserCreateForm form = new UserCreateForm();
        form.setName("testuser");
        form.setEmail("test@example.com");
        form.setPassword("password123");

        String jsonContent = objectMapper.writeValueAsString(form);

        // when & then
        mockMvc.perform(post("/api/appuser/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk()) // 현재는 200 OK, 향후 201 Created로 개선 가능
                .andExpect(jsonPath("$.message").value("User registered successfully!"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_fail_duplicate_email() throws Exception {
        // given: 먼저 사용자를 한 명 가입시킨다.
        UserCreateForm form1 = new UserCreateForm();
        form1.setName("existingUser");
        form1.setEmail("duplicate@example.com");
        form1.setPassword("password123");
        mockMvc.perform(post("/api/appuser/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(form1)));

        // when & then: 같은 이메일로 다시 가입을 시도한다.
        UserCreateForm form2 = new UserCreateForm();
        form2.setName("newUser");
        form2.setEmail("duplicate@example.com");
        form2.setPassword("password456");

        mockMvc.perform(post("/api/appuser/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(form2)))
                .andExpect(status().isConflict()) // 409 Conflict 예상 (예외 처리 개선 후)
                .andDo(print());
    }
}