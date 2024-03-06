package com.employee.management.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ForgetPasswordRequest {
    private String empId;
    private String otp;
    private String newPassword;

}
