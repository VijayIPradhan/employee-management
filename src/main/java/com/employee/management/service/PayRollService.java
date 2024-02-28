package com.employee.management.service;

import com.employee.management.DTO.PaySlip;
import com.employee.management.models.Payroll;

public interface PayRollService {
    PaySlip getPaySlip(String empId, String payPeriod);
}
