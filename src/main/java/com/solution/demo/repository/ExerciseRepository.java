package com.solution.demo.repository;

import com.solution.demo.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    Exercise findByExerciseName(String exerciseName);
}
