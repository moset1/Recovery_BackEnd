package com.solution.demo.service;

import com.solution.demo.entity.Exercise;
import com.solution.demo.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;

    public Exercise findByExerciseName(String exerciseName) {
        return exerciseRepository.findByExerciseName(exerciseName);
    }
}
