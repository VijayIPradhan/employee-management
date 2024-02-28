package com.employee.management.DTO;

import lombok.Data;

@Data
public class AuthRequest {
    private String empId;
    private String password;
}
