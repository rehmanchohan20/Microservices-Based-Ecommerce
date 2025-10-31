package com.microservice.auth.vo.base;

import lombok.Data;
import java.util.Date;

@Data
public abstract class BaseEntity {
    private Long id;
    private Date createdDate;
    private Date updatedDate;
    private Boolean isActive = true;

}
