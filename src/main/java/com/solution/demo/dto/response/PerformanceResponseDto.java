package com.solution.demo.dto.response;

import com.solution.demo.entity.Performance;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PerformanceResponseDto {
    private final Long performanceId;
    private final String userEmail;
    private final String exerciseName;
    private final LocalDate date;
    private final String score;

    public PerformanceResponseDto(Performance performance) {
        this.performanceId = performance.getPerformanceId();
        this.userEmail = performance.getAppUser().getEmail();
        this.exerciseName = performance.getExercise().getExerciseName();
        this.date = performance.getDate();
        this.score = performance.getScore();
    }
}