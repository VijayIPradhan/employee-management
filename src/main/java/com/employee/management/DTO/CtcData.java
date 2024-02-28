package com.employee.management.DTO;

import lombok.Data;

@Data
public class CtcData{
    private String yearlyGrossCtc;
    private String yearlyHRA;
    private String yearlyMedAllowance;
    private String yearlyOtherAllowance;
    private String yearlyBasic;

    private String monthlyGrossCtc;
    private String monthlyHRA;
    private String monthlyMedAllowance;
    private String monthlyOtherAllowance;
    private String monthlyBasic;

    private String monthlyIncomeTax;
    private String monthlyProfessionalTax;
    private String monthlyProvidentFund;
    private String monthlyLeaveDeduction;
    private String monthlyTotalDeduction;
    private String monthlyNetPayable;

    private String yearlyIncomeTax;
    private String yearlyProfessionalTax;
    private String yearlyProvidentFund;
    private String yearlyLeaveDeduction;
    private String yearlyTotalDeduction;
    private String yearlyNetPayable;
}
