package com.employee.management.service.impl;

import com.employee.management.DTO.ChangePasswordRequest;
import com.employee.management.DTO.EmployeeDTO;
import com.employee.management.DTO.ForgetPasswordRequest;
import com.employee.management.converters.Mapper;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.models.Employee;
import com.employee.management.models.OtpEntity;
import com.employee.management.repository.EmployeeRepository;
import com.employee.management.repository.OtpRepository;
import com.employee.management.service.EmailSenderService;
import com.employee.management.service.EmployeeService;
import com.employee.management.util.PasswordGenerator;
import com.employee.management.util.Verification;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    Mapper mapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    OtpRepository otpRepository;
    @Autowired
    PasswordGenerator passwordGenerator;
    @Autowired
    EmailSenderService emailSenderService;
    @Autowired
    Verification verification;
    @Override
    public EmployeeDTO getEmployee(String id){
       Employee employee= employeeRepository.findById(id).orElseThrow(()->new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));
      return mapper.convertToEmployeeDTO(employee);
    }
    @Override
    public boolean verifyUser(Long id, String password) {
        Employee employee=employeeRepository.findById("")
                .orElseThrow(()->new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));
        if(employee.getPassword().equals(password) && employee.getStatus().getName().equals("active")){
            return true;
        }
        if(employee.getStatus().getName().equals("inactive")){
            throw new CompanyException(ResCodes.INACTIVE_EMPLOYEE);
        }
        return false;
    }
    @Override
    public String changePassword(ChangePasswordRequest request) {
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            request.getEmployeeId(), request.getOldPassword()));
            if (authenticate.isAuthenticated()) {
                Employee employee = employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() -> new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));
                employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
                employeeRepository.save(employee);
                return "Success";
            } else {
                throw new CompanyException(ResCodes.INVALID_ID_AND_PASSWORD);
            }
        } catch (AuthenticationException e) {
            throw new CompanyException(ResCodes.INVALID_ID_AND_PASSWORD);
        }
    }
    @Override
    public String resetPasswordMail(String empId){
        Employee employee=employeeRepository.findById(empId)
                .orElseThrow(()->new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));

        otpRepository.findByEmployee(employee).ifPresent(prevOtp -> otpRepository.delete(prevOtp));

        OtpEntity otpEntity=new OtpEntity();
        otpEntity.setEmployee(employee);
        otpEntity.setOtpValue(passwordGenerator.generatePassword(5));
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expiryTime = currentTime.plusMinutes(2);
        otpEntity.setExpiryTime(expiryTime);
        otpRepository.save(otpEntity);
        emailSenderService.sendSimpleEmail(employee.getEmail(),"Forget Your Password",
                "Otp :"+otpEntity.getOtpValue()
                );
        return "Success";
    }

    @Override
    public String forgetPassword(ForgetPasswordRequest request){
        if(verification.verifyOtp(request.getEmpId(),request.getOtp())){
            Employee employee=employeeRepository.findById(request.getEmpId())
                    .orElseThrow(()->new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));
            employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
            employeeRepository.save(employee);
            otpRepository.delete(otpRepository.findByEmployee(employee).get());
            return "Success";
        }
        throw new CompanyException(ResCodes.INVALID_OTP);
    }





}
