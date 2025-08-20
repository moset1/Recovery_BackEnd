package com.solution.demo.service;

import com.solution.demo.entity.Exercise;
import com.solution.demo.repository.ExerciseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @InjectMocks
    private ExerciseService exerciseService;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Test
    @DisplayName("운동 이름으로 조회 성공")
    void findByExerciseName_Success() {
        // given (주어진 상황)
        String targetName = "팔 들어올리기";
        Exercise expectedExercise = new Exercise();
        expectedExercise.setExerciseId(1L);
        expectedExercise.setExerciseName(targetName);
        expectedExercise.setPrecautions("테스트 주의사항");

        given(exerciseRepository.findByExerciseName(targetName)).willReturn(Optional.of(expectedExercise));

        // when (행동)
        Exercise foundExercise = exerciseService.findByExerciseName(targetName);

        // then (결과)
        assertThat(foundExercise).isNotNull();
        assertThat(foundExercise.getExerciseName()).isEqualTo(targetName);
        assertThat(foundExercise.getPrecautions()).isEqualTo("테스트 주의사항");
        verify(exerciseRepository).findByExerciseName(targetName); // repository의 메소드가 호출되었는지 검증
    }

    @Test
    @DisplayName("운동 이름으로 조회 실패 (결과 없음)")
    void findByExerciseName_NotFound() {
        // given
        String nonExistentName = "존재하지 않는 운동";
        given(exerciseRepository.findByExerciseName(nonExistentName)).willReturn(Optional.empty());

        // when
        Exercise foundExercise = exerciseService.findByExerciseName(nonExistentName);

        // then
        assertThat(foundExercise).isNull();
        verify(exerciseRepository).findByExerciseName(nonExistentName);
    }
}