package com.chirayu.resumeanalyzer.service;

import com.chirayu.resumeanalyzer.dto.*;
import com.chirayu.resumeanalyzer.model.User;
import com.chirayu.resumeanalyzer.repository.UserRepository;
import com.chirayu.resumeanalyzer.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository repo, PasswordEncoder encoder, JwtService jwt){
        this.repo=repo; this.encoder=encoder; this.jwt=jwt;
    }

    public AuthResponse register(RegisterRequest r){
        if(repo.existsByEmail(r.getEmail())) throw new IllegalArgumentException("Email already registered");
        User u=User.builder().name(r.getName()).email(r.getEmail())
                .password(encoder.encode(r.getPassword())).role(User.Role.USER).build();
        u=repo.save(u);
        return new AuthResponse(jwt.generate(u.getId(),u.getEmail()),u.getId(),u.getName(),u.getEmail());
    }

    public AuthResponse login(LoginRequest r){
        User u=repo.findByEmail(r.getEmail()).orElseThrow(()->new IllegalArgumentException("Invalid credentials"));
        if(!encoder.matches(r.getPassword(),u.getPassword())) throw new IllegalArgumentException("Invalid credentials");
        return new AuthResponse(jwt.generate(u.getId(),u.getEmail()),u.getId(),u.getName(),u.getEmail());
    }
}
