package com.employee.management.DTO;

import lombok.Data;

@Data
public class OtpRequest {
    private String employeeId;
    private String otp;
}
