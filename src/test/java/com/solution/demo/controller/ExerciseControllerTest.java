package com.solution.demo.controller;

import com.solution.demo.entity.Exercise;
import com.solution.demo.service.ExerciseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExerciseController.class)
class ExerciseControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @MockBean
    private ExerciseService exerciseService;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(new CharacterEncodingFilter("UTF-8", true)) // 한글 깨짐 방지 필터 추가
                .alwaysDo(print()) // 항상 요청/응답 출력
                .build();
    }

    @Test
    @DisplayName("운동 상세 정보 조회 성공 (200 OK)")
    void getExercise_Success() throws Exception {
        // given
        String exerciseName = "팔 들어올리기";
        Exercise mockExercise = new Exercise();
        mockExercise.setExerciseId(1L);
        mockExercise.setExerciseName(exerciseName);
        mockExercise.setPrecautions("어깨에 통증이 느껴지면 중단하세요.");

        given(exerciseService.findByExerciseName(exerciseName)).willReturn(mockExercise);

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/exercises/{exerciseName}", exerciseName)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exerciseName").value(exerciseName))
                .andExpect(jsonPath("$.precautions").value("어깨에 통증이 느껴지면 중단하세요."));
    }

    @Test
    @DisplayName("운동 상세 정보 조회 실패 (404 Not Found)")
    void getExercise_NotFound() throws Exception {
        // given
        String exerciseName = "존재하지 않는 운동";
        given(exerciseService.findByExerciseName(exerciseName)).willReturn(null);

        // when & then
        mockMvc.perform(get("/api/exercises/{exerciseName}", exerciseName))
                .andExpect(status().isNotFound());
    }
}