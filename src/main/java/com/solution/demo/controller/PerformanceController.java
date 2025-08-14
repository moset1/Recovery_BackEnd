package com.solution.demo.controller;

import com.solution.demo.dto.request.PerformanceSaveRequestDto;
import com.solution.demo.dto.response.PerformanceResponseDto;
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
    // 날짜별 performance log
    @GetMapping("/daily")
    public ResponseEntity<List<PerformanceResponseDto>> getPerformancesByDate(
            @RequestParam String email,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<PerformanceResponseDto> performances = performanceService.findPerformancesByAppUserEmailAndDate(email, date);
        return ResponseEntity.ok(performances);
    }

    // 기간에 따른 performance log
    @GetMapping("/period")
    public ResponseEntity<List<PerformanceResponseDto>> getPerformancesByPeriod(
            @RequestParam String email,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<PerformanceResponseDto> performances = performanceService.findPerformancesByAppUserEmailAndDateBetween(email, startDate, endDate);
        return ResponseEntity.ok(performances);
    }

    @PostMapping("/save")
    public ResponseEntity<PerformanceResponseDto> savePerformance(@RequestBody PerformanceSaveRequestDto requestDto) {
        PerformanceResponseDto savedPerformanceDto = performanceService.savePerformance(requestDto);
        return new ResponseEntity<>(savedPerformanceDto, HttpStatus.CREATED);
    }
}
