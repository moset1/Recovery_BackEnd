package com.solution.demo.service;

import com.solution.demo.entity.Performance;
import com.solution.demo.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    public List<Performance> findPerformancesByAppUserEmailAndDate(String appUserEmail, LocalDate date) {
        return performanceRepository.findByAppUserEmailAndDate(appUserEmail, date);
    }

    public List<Performance> findPerformancesByAppUserEmailAndDateBetween(String appUserEmail, LocalDate startDate, LocalDate endDate) {
        return performanceRepository.findByAppUserEmailAndDateBetween(appUserEmail, startDate, endDate);
    }

    public Performance savePerformance(Performance performance) {
        return performanceRepository.save(performance);
    }
}
