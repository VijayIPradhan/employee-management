package com.employee.management.converters;

import com.employee.management.DTO.CtcData;
import com.employee.management.DTO.EmployeeDTO;
import com.employee.management.DTO.OfferLetterDTO;
import com.employee.management.DTO.PayrollDTO;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.models.Employee;
import com.employee.management.models.OfferLetterEntity;
import com.employee.management.models.Payroll;
import com.employee.management.models.Role;
import com.employee.management.util.CtcCalculator;
import com.employee.management.util.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

@Component
public class Mapper {
    @Autowired
    PasswordGenerator passwordGenerator;
    @Autowired
    DateTimeConverter dateConverter;
    @Autowired
    IndianNumberSystem numberFormat;
    @Autowired
    CtcCalculator calculator;
    public EmployeeDTO convertToEmployeeDTO(Employee employee){
        EmployeeDTO employeeDTO=new EmployeeDTO();
        if(employee!=null){
            employeeDTO.setRoles(employee.getRoles()
                    .stream()
                    .map(Role::getName)
                    .toList()
            );
            employeeDTO.setEmployeeID(employee.getEmployeeID());
            employeeDTO.setEmployeeName(employee.getEmployeeName());
            employeeDTO.setDesignation(employee.getDesignation());
            employeeDTO.setEmail(employee.getEmail());
            employeeDTO.setLocation(employee.getLocation());
            employeeDTO.setBankName(employee.getBankName());
            employeeDTO.setAccountNo(employee.getAccountNo());
            employeeDTO.setStatus(employee.getStatus().getName());
            employeeDTO.setPfNumber(employee.getPfNumber());
            employeeDTO.setUanNumber(employee.getUanNumber());
            employeeDTO.setDateOfJoin(dateConverter.localDateTimeToStringConverter(employee.getDateOfJoin()));
        }
        return employeeDTO;
    }
    public PayrollDTO convertToPayRollDTO(Payroll payroll) {
        PayrollDTO dto = new PayrollDTO();
        dto.setId(payroll.getId());
        dto.setPayPeriod(payroll.getPayPeriod());
        dto.setPayDate(dateConverter.localDateTimeToStringConverter(payroll.getPayDate()));
        dto.setEmployeeId(payroll.getEmployee().getEmployeeID());
        dto.setBasic(formatAmountWithCommas(payroll.getBasic()));
        dto.setHouseRentAllowance(formatAmountWithCommas(payroll.getHouseRentAllowance()));
        dto.setMedicalAllowance(formatAmountWithCommas(payroll.getMedicalAllowance()));
        dto.setOtherAllowance(formatAmountWithCommas(payroll.getOtherAllowance()));
        dto.setGrossEarnings(formatAmountWithCommas(payroll.getGrossEarnings()));
        dto.setProvidentFund(formatAmountWithCommas(payroll.getProvidentFund()));
        dto.setProfessionalTax(formatAmountWithCommas(payroll.getProfessionalTax()));
        dto.setLeaveDeduction(formatAmountWithCommas(payroll.getLeaveDeduction()));
        dto.setIncomeTax(formatAmountWithCommas(payroll.getIncomeTax()));
        dto.setTotalDeductions(formatAmountWithCommas(payroll.getTotalDeductions()));
        dto.setTotalNetPayable(formatAmountWithCommas(payroll.getTotalNetPayable()));
        dto.setTotalDaysPaid(payroll.getTotalPaidDays());
        dto.setTotalLopDays(payroll.getTotalLopDays());
        return dto;
    }

    public Payroll convertToPayroll(PayrollDTO payrollDTO){
        Payroll payroll=new Payroll();
        if(payrollDTO !=null) {
            payroll.setPayDate(dateConverter.stringToLocalDateTimeConverter(payrollDTO.getPayDate()));
            payroll.setPayPeriod(payrollDTO.getPayPeriod());
            CtcData ctcData = calculator.compensationDetails(convertStringToDoubleAmount(payrollDTO.getGrossEarnings()));
            payroll.setBasic((double) Math.round(ctcData.getMonthlyBasic()));
            payroll.setHouseRentAllowance((double) Math.round(ctcData.getMonthlyHRA()));
            payroll.setMedicalAllowance((double) Math.round(ctcData.getMonthlyMedAllowance()));
            payroll.setOtherAllowance((double) Math.round(ctcData.getMonthlyOtherAllowance()));
            payroll.setGrossEarnings((double) Math.round(ctcData.getMonthlyGrossCtc()));
            payroll.setLeaveDeduction(convertStringToDoubleAmount(payrollDTO.getLeaveDeduction()));
            payroll.setProfessionalTax((double) Math.round(ctcData.getMonthlyProfessionalTax()));
            payroll.setProvidentFund((double) Math.round(ctcData.getMonthlyProvidentFund()));
            payroll.setTotalDeductions((double) Math.round(ctcData.getMonthlyTotalDeduction()) + payroll.getLeaveDeduction());
            payroll.setTotalNetPayable((double) Math.round(ctcData.getMonthlyNetPayable())- payroll.getLeaveDeduction());
            payroll.setTotalPaidDays(payrollDTO.getTotalDaysPaid());
            payroll.setTotalLopDays(payrollDTO.getTotalLopDays());
            payroll.setIncomeTax((double) Math.round(ctcData.getMonthlyIncomeTax()));

            return payroll;
        }
        return payroll;
    }


    public Employee convertToEmployeeEntity(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        if(!validateEmployeeDto(employeeDTO)){
            throw new CompanyException(ResCodes.INVALID_EMPLOYEE_DETAILS);
        }
        employee.setEmployeeName(employeeDTO.getEmployeeName());
        employee.setDesignation(employeeDTO.getDesignation());
        employee.setLocation(employeeDTO.getLocation());
        employee.setBankName(employeeDTO.getBankName());
        employee.setAccountNo(employeeDTO.getAccountNo());
        employee.setPassword(passwordGenerator.generatePassword(6));
        employee.setEmail(employeeDTO.getEmail());
        employee.setDateOfJoin(dateConverter.stringToLocalDateTimeConverter(employeeDTO.getDateOfJoin()));
        employee.setPfNumber(employeeDTO.getPfNumber());
        employee.setUanNumber(employeeDTO.getUanNumber());
        return employee;
    }

    public OfferLetterEntity convertToOfferLetterEntity(OfferLetterDTO offerLetterDTO){
        OfferLetterEntity offerLetter=new OfferLetterEntity();
        if(offerLetterDTO!=null){
            offerLetter.setCtc(convertStringToDoubleAmount(offerLetterDTO.getCtc()));
            offerLetter.setFullName(offerLetterDTO.getFullName());
            offerLetter.setEmail(offerLetterDTO.getEmail());
            offerLetter.setJoiningDate(dateConverter
                    .stringToLocalDateTimeConverter(offerLetterDTO.getJoiningDate()));
            offerLetter.setIssuedDate(new Date());
            offerLetter.setPhoneNumber(offerLetterDTO.getPhoneNumber());
            offerLetter.setDesignation(offerLetterDTO.getDesignation());
            offerLetter.setDepartment(offerLetterDTO.getDepartment());
            return offerLetter;
        }
        throw new RuntimeException("OfferLetter DTO is null");
    }

    public OfferLetterDTO convertToOfferLetterDto(OfferLetterEntity entity){
        OfferLetterDTO offerLetterDTO=new OfferLetterDTO();
        offerLetterDTO.setCtc(String.valueOf(entity.getCtc()));
        offerLetterDTO.setIssuedDate(dateConverter.localDateTimeToStringConverter(entity.getIssuedDate()));
        offerLetterDTO.setFullName(entity.getFullName());
        offerLetterDTO.setEmail(entity.getEmail());
        offerLetterDTO.setDesignation(entity.getDesignation());
        offerLetterDTO.setJoiningDate(dateConverter.localDateTimeToStringConverter(entity.getJoiningDate()));
        offerLetterDTO.setPhoneNumber(entity.getPhoneNumber());
        offerLetterDTO.setDepartment(entity.getDepartment());
        return offerLetterDTO;
    }
    private boolean validateEmployeeDto(EmployeeDTO employeeDTO) {
        return Arrays.asList(employeeDTO.getEmployeeName(), employeeDTO.getDesignation(),
                        employeeDTO.getLocation(), employeeDTO.getBankName(),
                        employeeDTO.getAccountNo())
                .stream()
                .allMatch(field -> field != null && !field.isEmpty());
    }
    public Double convertStringToDoubleAmount(String amount){
        if(amount!=null && amount.contains(","))
            amount=amount.replace(",","");
       return Double.parseDouble(amount);
    }
    public String formatAmountWithCommas(Double amount) {
        if (amount == null) {
            return "";
        }
        if(amount==0){
            return "0";
        }
        String format=numberFormat.formatNumber(amount);
        return format;
    }

}
