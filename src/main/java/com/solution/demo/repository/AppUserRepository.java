package com.solution.demo.repository;

import com.solution.demo.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByEmail(String email);
    Optional<AppUser> findByName(String username);
}
