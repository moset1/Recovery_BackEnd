package com.solution.demo.controller;

import com.solution.demo.service.AppUserService;
import com.solution.demo.user.UserCreateForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/appuser")
public class AppUserController {

    private final AppUserService appUserService;


    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserCreateForm userCreateForm) {
        try {
            appUserService.create(userCreateForm.getName(),
                    userCreateForm.getEmail(), userCreateForm.getPassword());
            return ResponseEntity.ok("User registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
