package com.solution.demo.controller;

import com.solution.demo.service.AppUserService;
import com.solution.demo.user.UserCreateForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/appuser")
public class AppUserController {

    private final AppUserService appUserService;


    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@Valid @RequestBody UserCreateForm userCreateForm) {
        appUserService.create(userCreateForm.getName(),
                userCreateForm.getEmail(), userCreateForm.getPassword());

        Map<String, String> responseBody = Map.of("message", "User registered successfully!");
        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    }

}
