package com.employee.management.DTO;

import lombok.Data;
import java.util.Date;

@Data
public class PayrollDTO {
    private Long id;
    private String payPeriod;
    private String payDate;
    private String employeeId;
    private String basic;
    private String houseRentAllowance;
    private String medicalAllowance;
    private String otherAllowance;
    private String grossEarnings;
    private String providentFund;
    private String professionalTax;
    private String leaveDeduction;
    private String incomeTax;
    private String totalDeductions;
    private String totalNetPayable;
    private Integer totalDaysPaid;
    private Integer totalLopDays;
}
