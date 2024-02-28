package com.employee.management.DTO;

import lombok.Data;

@Data
public class PaySlipRequest {
    private String employeeId;
    private String payPeriod;
}