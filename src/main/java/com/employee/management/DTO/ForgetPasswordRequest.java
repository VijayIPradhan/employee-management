package com.employee.management.DTO;

import lombok.Data;

@Data
public class ForgetPasswordRequest {
    private String empId;
    private String otp;
    private String newPassword;
}
