package com.solution.demo.service;

import com.solution.demo.dto.request.PerformanceSaveRequestDto;
import com.solution.demo.dto.response.PerformanceResponseDto;
import com.solution.demo.entity.AppUser;
import com.solution.demo.entity.Exercise;
import com.solution.demo.entity.Performance;
import com.solution.demo.exception.ResourceNotFoundException;
import com.solution.demo.repository.AppUserRepository;
import com.solution.demo.repository.ExerciseRepository;
import com.solution.demo.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final AppUserRepository appUserRepository;
    private final ExerciseRepository exerciseRepository;

    @Transactional(readOnly = true)
    public List<PerformanceResponseDto> findPerformancesByAppUserEmailAndDate(String appUserEmail, LocalDate date) {
        return performanceRepository.findByAppUserEmailAndDate(appUserEmail, date).stream()
                .map(PerformanceResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PerformanceResponseDto> findPerformancesByAppUserEmailAndDateBetween(String appUserEmail, LocalDate startDate, LocalDate endDate) {
        return performanceRepository.findByAppUserEmailAndDateBetween(appUserEmail, startDate, endDate).stream()
                .map(PerformanceResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public PerformanceResponseDto savePerformance(PerformanceSaveRequestDto requestDto) {
        AppUser appUser = appUserRepository.findByEmail(requestDto.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + requestDto.getUserEmail()));

        Exercise exercise = exerciseRepository.findByExerciseName(requestDto.getExerciseName())
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with name: " + requestDto.getExerciseName()));

        Performance performance = new Performance(appUser, exercise, requestDto.getScore());
        Performance savedPerformance = performanceRepository.save(performance);

        return new PerformanceResponseDto(savedPerformance);
    }
}
