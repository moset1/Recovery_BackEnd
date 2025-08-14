package com.solution.demo.controller;

import com.solution.demo.entity.Exercise;
import com.solution.demo.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping("/{exerciseName}")
    public ResponseEntity<Exercise> getExercise(@PathVariable String exerciseName) {
        Exercise exercise = exerciseService.findByExerciseName(exerciseName);

        if (exercise == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(exercise);
    }
}
