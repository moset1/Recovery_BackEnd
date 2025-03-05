package com.solution.demo.controller;

import com.solution.demo.entity.AppUser;
import com.solution.demo.entity.Exercise;
import com.solution.demo.entity.Performance;
import com.solution.demo.repository.PerformanceRepository;
import com.solution.demo.service.AppUserService;
import com.solution.demo.service.ExerciseService;
import com.solution.demo.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    private final PerformanceService performanceService;

    private final AppUserService appUserService;
    private final ExerciseService exerciseService;
    // 날짜별 performance log
    @GetMapping("/daily")
    public ResponseEntity<List<Performance>> getPerformancesByAppUserEmailAndDate(
            @RequestParam String email,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Performance> performances = performanceService.findPerformancesByAppUserEmailAndDate(email, date);
        return new ResponseEntity<>(performances, HttpStatus.OK);
    }

    // 기간에 따른 performance log
    @GetMapping("/period")
    public ResponseEntity<List<Performance>> getPerformancesByAppUserEmailAndDateBetween(
            @RequestParam String email,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Performance> performances = performanceService.findPerformancesByAppUserEmailAndDateBetween(email, startDate, endDate);
        return new ResponseEntity<>(performances, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<Performance> savePerformance(@RequestBody Performance performance) {

        AppUser appUser = appUserService.findByEmail(performance.getAppUser().getEmail());
        Exercise exercise = exerciseService.findByExerciseName(performance.getExercise().getExerciseName());
        performance.setDate(LocalDate.now());
        performance.setAppUser(appUser);
        performance.setExercise(exercise);
        Performance savedPerformance = performanceService.savePerformance(performance);
        return new ResponseEntity<>(savedPerformance, HttpStatus.CREATED);
    }
}
