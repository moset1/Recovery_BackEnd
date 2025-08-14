package com.solution.demo.service;

import com.solution.demo.entity.Exercise;
import com.solution.demo.exception.ResourceNotFoundException;
import com.solution.demo.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;

    @Transactional(readOnly = true)
    public Exercise findByExerciseName(String exerciseName) {
        return exerciseRepository.findByExerciseName(exerciseName)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with name: " + exerciseName));
    }
}
