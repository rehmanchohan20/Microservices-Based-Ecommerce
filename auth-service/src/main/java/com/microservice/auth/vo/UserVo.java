package com.microservice.auth.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.microservice.auth.enums.RoleEnums;
import com.microservice.auth.vo.base.BaseEntity;
import lombok.Data;

@Data
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserVo extends BaseEntity {
    private String username;
    private String password;
    private RoleEnums role;
}
