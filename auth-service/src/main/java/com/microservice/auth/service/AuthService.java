package com.microservice.auth.service;

import com.microservice.auth.model.User;
import com.microservice.auth.repo.UserRepository;
import com.microservice.auth.response.Response;
import com.microservice.auth.util.JwtUtil;
import com.microservice.auth.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Transactional
    public Response register(UserVo userVo) {
        boolean isUpdate = userVo.getId() != null && userVo.getId() > 0;
        validations(userVo, isUpdate);
        User user = getOrCreateUser(userVo.getId());
        mapToEntity(userVo, user);
        User save = userRepository.save(user);
        UserVo mapped = mapToVo(save);
        return Response.setResponse(true, "registered successfully", mapped);
    }


    public Response login(UserVo userVo) {
        Map<String, Object> map = new LinkedHashMap<>();
        loginValidations(userVo);
        User loginUser = getLoginUser(userVo);
        String token = jwtUtil.generateToken(loginUser.getUsername(), loginUser.getRole().name(), loginUser.getId());
        UserVo mapped = mapToVo(loginUser);
        map.put("user", mapped);
        map.put("token", token);
        return Response.setResponse(true, "login successful", map);
    }

    public Response listUsers() {
        Map<String, Object> map = new LinkedHashMap<>();
        List<UserVo> list = getUsersVoSortedByIdDesc();
        map.put("users", list);
        return Response.setResponse(true, "user list fetched", map);
    }

    public Response validateToken(String auth) {
        String token = auth.replace("Bearer ","");
        boolean ok = jwtUtil.validateToken(token);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("valid", ok);
        return Response.setResponse(true, "token validation checked", map);
    }

    public Response getUser(Long id){
        User user = getOrCreateUser(id);
        UserVo mapped = mapToVo(user);
        return Response.setResponse(true, "user fetched successfully", mapped);
    }


///    helper methods for above services for clean and readable code

    private User getOrCreateUser(Long id) {
        if (id == null) {
            return new User();
        } else {
            return userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        }
    }

    private User getLoginUser(UserVo userVo) {
        User user = userRepository.findByUsername(userVo.getUsername())
                .orElseThrow(() -> new RuntimeException("invalid credentials"));
        if (!passwordEncoder.matches(userVo.getPassword(), user.getPassword())) {
            throw new RuntimeException("invalid credentials");
        }
        if(!user.getIsActive()){
            throw new RuntimeException("user is not active, Can't Login");
        }
        return user;
    }

    private void loginValidations(UserVo userVo) {
        if(Objects.isNull(userVo) || Objects.isNull(userVo.getUsername()) || Objects.isNull(userVo.getPassword())){
            throw new RuntimeException("data should not be null");
        }
    }

    private void validations(UserVo userVo, boolean isUpdate) {
        if(Objects.isNull(userVo)) {
            throw new RuntimeException("user is null");
        }
        if(!isUpdate){
            if(userRepository.findByUsername(userVo.getUsername()).isPresent()){
                throw new RuntimeException("username exists");
            }
        }else{
            if(userVo.getId() == null || userVo.getId() < 0){
                throw new RuntimeException("id is null");
            }
        }
    }


    private List<User> fetchUsersSortedByIdDesc() {
        return Optional.of(userRepository.findAll(Sort.by(Sort.Direction.DESC, "id")))
            .orElse(Collections.emptyList());
    }


    private List<UserVo> getUsersVoSortedByIdDesc() {
        List<User> users = fetchUsersSortedByIdDesc();
        return users.stream().map(this::mapToVo).toList();
    }

    private void mapToEntity(UserVo userVo,  User user) {
        user.setId(userVo.getId());
        user.setUsername(userVo.getUsername());
        if (userVo.getPassword() != null && !userVo.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userVo.getPassword()));
        }
        user.setRole(userVo.getRole());
        user.setIsActive(userVo.getIsActive());
    }

    private UserVo mapToVo(User user) {
        UserVo userVo = new UserVo();
        userVo.setId(user.getId());
        userVo.setUsername(user.getUsername());
        userVo.setCreatedDate(user.getCreatedDate());
        userVo.setRole(user.getRole());
        userVo.setUpdatedDate(user.getUpdatedDate());
        userVo.setIsActive(user.getIsActive());
        return userVo;
    }
}