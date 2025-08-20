package com.solution.demo.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CacheTestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    @DisplayName("ETag 캐싱이 적용되어 304 Not Modified를 반환한다")
    void etagCachingTest() throws Exception {
        // 1. 첫 번째 요청을 보내 ETag를 획득한다.
        MvcResult firstResult = mockMvc.perform(get("/api/cache-test"))
                .andExpect(status().isOk())
                .andReturn();

        String etag = firstResult.getResponse().getHeader("ETag");
        assertThat(etag).isNotNull();

        // 2. 두 번째 요청에 ETag를 담아 보내 304 응답을 확인한다.
        mockMvc.perform(get("/api/cache-test").header("If-None-Match", etag))
                .andExpect(status().isNotModified());
    }
}