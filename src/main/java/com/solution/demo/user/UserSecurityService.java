package com.solution.demo.user;

import com.solution.demo.entity.AppUser;
import com.solution.demo.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = this.appUserRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을수 없습니다."));

        List<GrantedAuthority> authorities = new ArrayList<>();
        // TODO: 향후 AppUser 엔티티에 role 필드를 추가하여 동적으로 권한을 부여하는 방식으로 개선할 수 있다.
        if ("admin@google.com".equals(username)) { // 관리자 계정은 이메일로 구분
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        } else {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        }
        // Spring Security의 User 객체에는 로그인 ID(이메일), 비밀번호, 권한을 전달
        return new User(appUser.getEmail(), appUser.getPassword(), authorities);
    }
}