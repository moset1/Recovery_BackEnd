package com.solution.demo.controller;

import com.solution.demo.entity.Exercise;
import com.solution.demo.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/exercise")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping("/detail")
    public ResponseEntity<Exercise> getExercise(@RequestParam String exerciseName) {
        Exercise exercise = exerciseService.findByExerciseName(exerciseName);
        return new ResponseEntity<>(exercise, HttpStatus.OK);
    }
}
