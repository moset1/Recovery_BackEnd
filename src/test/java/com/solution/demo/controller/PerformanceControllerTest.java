package com.solution.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solution.demo.dto.request.PerformanceSaveRequestDto;
import com.solution.demo.entity.AppUser;
import com.solution.demo.entity.Exercise;
import com.solution.demo.entity.Performance;
import com.solution.demo.repository.AppUserRepository;
import com.solution.demo.repository.ExerciseRepository;
import com.solution.demo.repository.PerformanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
// API가 인증을 요구하므로, 테스트 시 인증된 사용자를 모의로 설정
@WithMockUser
public class PerformanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    private AppUser testUser;
    private Exercise testExercise;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 및 운동 데이터 생성
        testUser = new AppUser();
        testUser.setEmail("performance-user@example.com");
        testUser.setName("perform-user");
        testUser.setPassword("password");
        appUserRepository.save(testUser);

        testExercise = new Exercise();
        testExercise.setExerciseName("팔 들어올리기");
        testExercise.setPrecautions("어깨에 통증이 없는 범위까지만 수행하세요.");
        exerciseRepository.save(testExercise);
    }

    @Test
    @DisplayName("운동 기록 저장 성공")
    void savePerformance_success() throws Exception {
        // given
        PerformanceSaveRequestDto requestDto = new PerformanceSaveRequestDto();
        requestDto.setUserEmail(testUser.getEmail());
        requestDto.setExerciseName(testExercise.getExerciseName());
        requestDto.setScore("95");

        String jsonContent = objectMapper.writeValueAsString(requestDto);

        // when & then
        mockMvc.perform(post("/api/performance/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userEmail").value(testUser.getEmail()))
                .andExpect(jsonPath("$.exerciseName").value(testExercise.getExerciseName()))
                .andExpect(jsonPath("$.score").value("95"))
                .andDo(print());

        // DB에 실제로 저장되었는지 확인
        assertThat(performanceRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("운동 기록 저장 실패 - 존재하지 않는 사용자")
    void savePerformance_fail_user_not_found() throws Exception {
        // given
        PerformanceSaveRequestDto requestDto = new PerformanceSaveRequestDto();
        requestDto.setUserEmail("nonexistent-user@example.com");
        requestDto.setExerciseName(testExercise.getExerciseName());
        requestDto.setScore("90");

        String jsonContent = objectMapper.writeValueAsString(requestDto);

        // when & then
        mockMvc.perform(post("/api/performance/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with email: " + requestDto.getUserEmail()))
                .andDo(print());
    }

    @Test
    @DisplayName("일일 운동 기록 조회 성공")
    void getPerformancesByDate_success() throws Exception {
        // given
        // 테스트를 위해 특정 날짜에 운동 기록을 미리 저장
        Performance performance = new Performance(testUser, testExercise, "95");
        performance.setDate(LocalDate.of(2024, 5, 22));
        performanceRepository.save(performance);

        // when & then
        mockMvc.perform(get("/api/performance/daily")
                        .param("email", testUser.getEmail())
                        .param("date", "2024-05-22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].userEmail").value(testUser.getEmail()))
                .andExpect(jsonPath("$[0].exerciseName").value(testExercise.getExerciseName()))
                .andExpect(jsonPath("$[0].score").value("95"))
                .andDo(print());
    }

    @Test
    @DisplayName("일일 운동 기록 조회 - 해당 날짜에 기록이 없는 경우 빈 배열 반환")
    void getPerformancesByDate_whenNoRecords_returnsEmptyList() throws Exception {
        // given
        // 이 테스트에서는 데이터를 미리 저장하지 않음

        // when & then
        mockMvc.perform(get("/api/performance/daily")
                        .param("email", testUser.getEmail())
                        .param("date", "2024-05-23")) // 기록이 없는 날짜
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0))
                .andDo(print());
    }

    @Test
    @DisplayName("기간별 운동 기록 조회 성공")
    void getPerformancesByPeriod_success() throws Exception {
        // given
        // 테스트를 위해 여러 날짜에 운동 기록을 미리 저장
        Performance p1 = new Performance(testUser, testExercise, "90");
        p1.setDate(LocalDate.of(2024, 5, 20));
        performanceRepository.save(p1);

        Performance p2 = new Performance(testUser, testExercise, "85");
        p2.setDate(LocalDate.of(2024, 5, 21));
        performanceRepository.save(p2);

        // 기간에 포함되지 않는 기록
        Performance p3 = new Performance(testUser, testExercise, "99");
        p3.setDate(LocalDate.of(2024, 5, 23));
        performanceRepository.save(p3);

        // when & then
        mockMvc.perform(get("/api/performance/period")
                        .param("email", testUser.getEmail())
                        .param("startDate", "2024-05-20")
                        .param("endDate", "2024-05-22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(print());
    }

}