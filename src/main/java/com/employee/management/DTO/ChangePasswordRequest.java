package com.employee.management.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest{
    private String employeeId;
    private String oldPassword;
    private String newPassword;
}
