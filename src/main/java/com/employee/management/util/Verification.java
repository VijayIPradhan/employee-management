package com.employee.management.util;

import com.employee.management.DTO.OtpRequest;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.models.Employee;
import com.employee.management.models.OtpEntity;
import com.employee.management.repository.EmployeeRepository;
import com.employee.management.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Verification {
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    OtpRepository otpRepository;
    public boolean verifyOtp(String empId,String otp){
        Employee employee=employeeRepository.findById(empId)
                .orElseThrow(()->new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));
        OtpEntity otpEntity=otpRepository.findByEmployee(employee)
                .orElseThrow(()->new CompanyException(ResCodes.INVALID_OTP));
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expiryTime = otpEntity.getExpiryTime();
        return otpEntity.getOtpValue().equals(otp) && !currentTime.isAfter(expiryTime);
    }
}
