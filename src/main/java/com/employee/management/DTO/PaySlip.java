package com.employee.management.DTO;

import com.employee.management.models.Payroll;
import lombok.Data;

@Data
public class PaySlip {
    private EmployeeDTO employeeDTO;
    private PayrollDTO payrollDTO;
}
