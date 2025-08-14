package com.solution.demo.repository;

import com.solution.demo.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    // 이메일로 사용자를 조회하고, 결과가 없을 수도 있음을 Optional로 명시
    Optional<AppUser> findByEmail(String email);
}
