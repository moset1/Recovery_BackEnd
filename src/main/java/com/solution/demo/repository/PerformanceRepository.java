package com.solution.demo.repository;

import com.solution.demo.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    List<Performance> findByAppUserEmailAndDate(String appUserEmail, LocalDate date);
    List<Performance> findByAppUserEmailAndDateBetween(String appUserEmail,LocalDate startDate, LocalDate endDate);
}
