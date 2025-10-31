package com.microservice.auth.controller;

import com.microservice.auth.model.User;
import com.microservice.auth.repo.UserRepository;
import com.microservice.auth.response.Response;
import com.microservice.auth.service.AuthService;
import com.microservice.auth.util.JwtUtil;
import com.microservice.auth.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserVo userVo){
        Response response =  authService.register(userVo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody UserVo userVo){
        Response response =  authService.register(userVo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserVo userVo){
        Response response = authService.login(userVo);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<?> list(){
        Response response = authService.listUsers();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Long id){
        Response response = authService.getUser(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader("Authorization") String auth){
        Response response = authService.validateToken(auth);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
