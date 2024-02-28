package com.employee.management.controller;

import com.employee.management.DTO.ChangePasswordRequest;
import com.employee.management.DTO.EmployeeDTO;
import com.employee.management.DTO.ForgetPasswordRequest;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.service.AttendanceService;
import com.employee.management.service.EmployeeService;
import com.employee.management.util.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private JWTService jwtService;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    AttendanceService attendanceService;


    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<EmployeeDTO>getEmployee(@RequestParam("empId")String id, @RequestHeader("Authorization")String jwtToken){
        System.out.println("Token :"+jwtToken);
        String token=jwtToken.substring(7);
        String empId=jwtService.extractUsername(token);
        if(String.valueOf(id).equals(empId))
             return new ResponseEntity<>(employeeService.getEmployee(id), HttpStatus.OK);
        else throw new CompanyException(ResCodes.NOT_AUTHORIZED);
    }
}
