package com.campus.postgres.user.service;

import com.campus.postgres.dto.response.UserResponse;
import com.campus.postgres.user.entity.UserEntity;
import com.campus.postgres.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserAuthService implements UserDetailsService {
    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserEntity> check = userRepository.findByEmail(email);
        if (check.isEmpty()) throw new UsernameNotFoundException("user non found");

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("user"));
        UserEntity user = check.get();

        return new User(user.getEmail(), user.getPassword(), authorities);
    }
}
