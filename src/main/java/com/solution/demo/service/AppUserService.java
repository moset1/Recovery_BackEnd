package com.solution.demo.service;

import com.solution.demo.entity.AppUser;
import com.solution.demo.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    public void create(String name, String email, String password) {
        AppUser user = new AppUser();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.appUserRepository.save(user);
    }

    public AppUser findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }
}
