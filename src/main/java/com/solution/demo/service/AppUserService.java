package com.solution.demo.service;

import com.solution.demo.entity.AppUser;
import com.solution.demo.exception.ResourceNotFoundException;
import com.solution.demo.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    @Transactional
    public AppUser create(String name, String email, String password) {
        if (appUserRepository.findByEmail(email).isPresent()) {
            throw new DataIntegrityViolationException("이미 등록된 이메일입니다.");
        }
        AppUser user = new AppUser();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        return this.appUserRepository.save(user);
    }
    @Transactional(readOnly = true)
    public AppUser findByEmail(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
