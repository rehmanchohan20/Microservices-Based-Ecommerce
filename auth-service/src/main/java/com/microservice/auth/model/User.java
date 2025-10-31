package com.microservice.auth.model;

import com.microservice.auth.enums.RoleEnums;
import com.microservice.auth.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    private RoleEnums role;
}
