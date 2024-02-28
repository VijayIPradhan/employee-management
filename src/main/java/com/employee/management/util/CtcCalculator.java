package com.employee.management.util;

import com.employee.management.DTO.CtcData;
import com.employee.management.converters.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CtcCalculator {
    private static final double HRA_PERCENTAGE = 0.1;
    private static final double MED_PERCENTAGE = 0.3;
    private static final double IT_PERCENTAGE = 0;
    private static final double PF = 1000;
    private static final double BASIC = 8333;
    private static final double PROF_TAX = 200;
    private static final double LEAVE_DED = 0;


    public CtcData compensationDetails(Double grossSalary) {
        Double monthlyGrossSalary = grossSalary / 12;

        CtcData data = new CtcData();
        data.setYearlyGrossCtc(grossSalary);
        data.setMonthlyGrossCtc(monthlyGrossSalary);

        calculateMonthlyCompensations(data);

        Double monthlyHRA = monthlyGrossSalary * HRA_PERCENTAGE;
        Double monthlyMedAllowance = monthlyGrossSalary * MED_PERCENTAGE;
        Double monthlyOtherAllowance = monthlyGrossSalary - (monthlyMedAllowance + BASIC + monthlyHRA);

        data.setYearlyBasic(BASIC * 12);
        data.setMonthlyBasic(BASIC);
        data.setYearlyHRA(monthlyHRA * 12);
        data.setMonthlyHRA(monthlyHRA);
        data.setYearlyMedAllowance(monthlyMedAllowance * 12);
        data.setMonthlyMedAllowance(monthlyMedAllowance);
        data.setYearlyOtherAllowance(monthlyOtherAllowance * 12);
        data.setMonthlyOtherAllowance(monthlyOtherAllowance);
        data.setYearlyIncomeTax(data.getMonthlyIncomeTax()*12);
        data.setYearlyLeaveDeduction(data.getMonthlyLeaveDeduction()*12);
        data.setYearlyProvidentFund(data.getMonthlyProvidentFund()*12);
        data.setYearlyProfessionalTax(data.getMonthlyProfessionalTax()*12);
        data.setYearlyTotalDeduction(data.getMonthlyTotalDeduction()*12);
        data.setYearlyNetPayable(data.getYearlyGrossCtc()-data.getYearlyTotalDeduction());

        return data;
    }

    private void calculateMonthlyCompensations(CtcData data) {
        Double monthlyGrossSalary = data.getMonthlyGrossCtc();
        data.setMonthlyBasic(BASIC);
        data.setMonthlyHRA(monthlyGrossSalary * HRA_PERCENTAGE);

        Double monthlyMedAllowance = monthlyGrossSalary * MED_PERCENTAGE;
        Double monthlyOtherAllowance = monthlyGrossSalary - (monthlyMedAllowance + BASIC + data.getMonthlyHRA());

        data.setMonthlyMedAllowance(monthlyMedAllowance);
        data.setMonthlyOtherAllowance(monthlyOtherAllowance);
        data.setMonthlyIncomeTax(monthlyGrossSalary * IT_PERCENTAGE);
        data.setMonthlyProfessionalTax(PROF_TAX);
        data.setMonthlyLeaveDeduction(LEAVE_DED);
        data.setMonthlyProvidentFund(PF);
        Double totalDeduction = PROF_TAX + data.getMonthlyIncomeTax() + PF + LEAVE_DED;
        data.setMonthlyTotalDeduction(totalDeduction);
        data.setMonthlyNetPayable(monthlyGrossSalary - totalDeduction);
    }
}
