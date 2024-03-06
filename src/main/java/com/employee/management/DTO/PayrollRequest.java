package com.employee.management.DTO;

import lombok.Data;

import java.util.Date;
@Data

public class PayrollRequest {
    private String employeeId;
    private String payPeriod;
    private String payDate;
    private String grossEarnings;
    private String leaveDeduction;


}
