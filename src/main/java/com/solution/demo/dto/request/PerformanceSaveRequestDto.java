package com.solution.demo.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerformanceSaveRequestDto {
    private String userEmail;
    private String exerciseName;
    private String score;
}