package com.employee.management.DTO;

import lombok.Data;

@Data
public class ChangePasswordRequest{
    private String employeeId;
    private String oldPassword;
    private String newPassword;
}
