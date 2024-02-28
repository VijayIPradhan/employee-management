package com.employee.management.service;

import com.employee.management.DTO.ChangePasswordRequest;
import com.employee.management.DTO.EmployeeDTO;
import com.employee.management.DTO.ForgetPasswordRequest;

public interface EmployeeService {
    EmployeeDTO getEmployee(String id);
    @Deprecated
    boolean verifyUser(Long id, String password);

    String changePassword(ChangePasswordRequest request);

    String resetPasswordMail(String empId);

    String forgetPassword(ForgetPasswordRequest request);
}
