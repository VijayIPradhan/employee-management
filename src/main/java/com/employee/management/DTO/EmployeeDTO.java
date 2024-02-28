package com.employee.management.DTO;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EmployeeDTO {
    private String employeeID;
    private String employeeName;
    private String designation;
    private List<String> roles;
    private String location;
    private String bankName;
    private String accountNo;
    private String dateOfJoin;
    private String password;
    private String status;
    private String email;
    private String uanNumber;
    private String pfNumber;
}
