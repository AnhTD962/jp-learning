package com.domain.backend.security;

import com.domain.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .map(user -> User.withUsername(user.getUsername())
                        .password(user.getPassword())
                        .authorities(
                                user.getRoles().stream()
                                        .map(Enum::name)
                                        .toArray(String[]::new)
                        )
                        .accountLocked(!user.isAccountNonLocked()) // 👈 Quan trọng
                        .disabled(!user.isEnabled()) // nếu có field enabled
                        .build()
                );
    }
}