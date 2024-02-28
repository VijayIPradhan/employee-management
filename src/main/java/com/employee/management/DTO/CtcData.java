package com.employee.management.DTO;

import lombok.Data;

@Data
public class CtcData{
    private Double yearlyGrossCtc;
    private Double yearlyHRA;
    private Double yearlyMedAllowance;
    private Double yearlyOtherAllowance;
    private Double yearlyBasic;

    private Double monthlyGrossCtc;
    private Double monthlyHRA;
    private Double monthlyMedAllowance;
    private Double monthlyOtherAllowance;
    private Double monthlyBasic;

    private Double monthlyIncomeTax;
    private Double monthlyProfessionalTax;
    private Double monthlyProvidentFund;
    private Double monthlyLeaveDeduction;
    private Double monthlyTotalDeduction;
    private Double monthlyNetPayable;

    private Double yearlyIncomeTax;
    private Double yearlyProfessionalTax;
    private Double yearlyProvidentFund;
    private Double yearlyLeaveDeduction;
    private Double yearlyTotalDeduction;
    private Double yearlyNetPayable;
}
