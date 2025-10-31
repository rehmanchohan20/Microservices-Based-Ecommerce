package com.example.auth.controller;

import com.example.auth.model.User;
import com.example.auth.repo.UserRepository;
import com.example.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user){
        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            return ResponseEntity.badRequest().body(Map.of("error","username exists"));
        }
        // NOTE: password not hashed in this skeleton — add BCrypt in production
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("status","registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> payload){
        String username = payload.get("username");
        String password = payload.get("password");
        Optional<User> byUsername = userRepository.findByUsername(username);
        if(byUsername.isEmpty() || !byUsername.get().getPassword().equals(password)){
            return ResponseEntity.status(401).body(Map.of("error","invalid credentials"));
        }
        String token = jwtUtil.generateToken(username);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String auth){
        String token = auth.replace("Bearer ","");
        boolean ok = jwtUtil.validateToken(token);
        return ResponseEntity.ok(Map.of("valid", ok));
    }
}
