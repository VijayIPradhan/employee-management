package com.employee.management.util;

import com.employee.management.DTO.CtcData;
import com.employee.management.converters.IndianNumberSystem;
import com.employee.management.converters.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
        data.setYearlyGrossCtc((formatNumber(grossSalary)));
        data.setMonthlyGrossCtc((formatNumber(monthlyGrossSalary)));

        calculateMonthlyCompensations(data);

        Double monthlyHRA = monthlyGrossSalary * HRA_PERCENTAGE;
        Double monthlyMedAllowance = monthlyGrossSalary * MED_PERCENTAGE;
        Double monthlyOtherAllowance = monthlyGrossSalary - (monthlyMedAllowance + BASIC + monthlyHRA);

        data.setYearlyBasic((formatNumber(BASIC * 12)));
        data.setMonthlyBasic((formatNumber(BASIC)));
        data.setYearlyHRA((formatNumber(monthlyHRA*12)));
        data.setMonthlyHRA((formatNumber(monthlyHRA)));
        data.setYearlyMedAllowance((formatNumber(monthlyMedAllowance * 12)));
        data.setMonthlyMedAllowance((formatNumber(monthlyMedAllowance)));
        data.setYearlyOtherAllowance((formatNumber(monthlyOtherAllowance * 12)));
        data.setMonthlyOtherAllowance((formatNumber(monthlyOtherAllowance)));
        data.setYearlyIncomeTax((formatNumber(Double.parseDouble(data.getMonthlyIncomeTax())*12)));
        data.setYearlyLeaveDeduction((formatNumber(Double.parseDouble(data.getMonthlyLeaveDeduction())*12)));
        data.setYearlyProvidentFund((formatNumber(Double.parseDouble(data.getMonthlyProvidentFund())*12)));
        data.setYearlyProfessionalTax((formatNumber(Double.parseDouble(data.getMonthlyProfessionalTax())*12)));
        data.setYearlyTotalDeduction((formatNumber(Double.parseDouble(data.getMonthlyTotalDeduction())*12)));
        data.setYearlyNetPayable((formatNumber(Double.parseDouble(data.getYearlyGrossCtc())-Double.parseDouble(data.getYearlyTotalDeduction()))));

        return data;
    }
    private void calculateMonthlyCompensations(CtcData data) {
        Double monthlyGrossSalary = Double.valueOf(data.getMonthlyGrossCtc());
        data.setMonthlyBasic(formatNumber(BASIC));
        data.setMonthlyHRA(formatNumber(monthlyGrossSalary * HRA_PERCENTAGE));

        Double monthlyMedAllowance = monthlyGrossSalary * MED_PERCENTAGE;
        Double monthlyOtherAllowance = monthlyGrossSalary - (monthlyMedAllowance + BASIC + Double.parseDouble(data.getMonthlyHRA()));

        data.setMonthlyMedAllowance(formatNumber(monthlyMedAllowance));
        data.setMonthlyOtherAllowance(formatNumber(monthlyOtherAllowance));
        data.setMonthlyIncomeTax(formatNumber(monthlyGrossSalary * IT_PERCENTAGE));
        data.setMonthlyProfessionalTax(formatNumber(PROF_TAX));
        data.setMonthlyLeaveDeduction(formatNumber(LEAVE_DED));
        data.setMonthlyProvidentFund(formatNumber(PF));
        Double totalDeduction = PROF_TAX + Double.parseDouble(data.getMonthlyIncomeTax()) + PF + LEAVE_DED;
        data.setMonthlyTotalDeduction(formatNumber(totalDeduction));
        data.setMonthlyNetPayable(formatNumber(monthlyGrossSalary - totalDeduction));
    }

    private String formatNumber(Double value) {

        BigDecimal bd = new BigDecimal(Math.round(value)).setScale(2, RoundingMode.HALF_UP);
        String formattedValue = bd.toString();
        String[] parts = formattedValue.split("\\.");
        if (parts.length > 1 && parts[1].length() < 2) {
            parts[1] = parts[1] + "0".repeat(2 - parts[1].length());
        }
        formattedValue = String.join(".", parts);
        return formattedValue;
    }
}
