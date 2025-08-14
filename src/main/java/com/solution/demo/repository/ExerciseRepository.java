package com.solution.demo.repository;

import com.solution.demo.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    // 운동 이름으로 운동 정보를 조회하고, 결과가 없을 수도 있음을 Optional로 명시
    Optional<Exercise> findByExerciseName(String exerciseName);
}
