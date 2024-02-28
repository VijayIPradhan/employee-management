package com.employee.management.schedulers;

import ch.qos.logback.core.util.FixedDelay;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.models.Employee;
import com.employee.management.models.Payroll;
import com.employee.management.repository.AttendanceRepository;
import com.employee.management.repository.EmployeeRepository;
import com.employee.management.repository.PayrollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class PayrollUpdater {
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    PayrollRepository payrollRepository;
    @Autowired
    AttendanceRepository attendanceRepository;

    @Scheduled(cron = "0 0 19 L * ?",zone = "Asia/Kolkata")
//@Scheduled(fixedRate = 60000)
    public void updatePayroll(){
        List<Employee> employees=employeeRepository.findAll();
        employees.stream()
                .filter(Objects::nonNull)
                .map(this::generatePayRoll)
                .filter(payroll -> {
                  Payroll e=  payrollRepository.getPayPeriodDetails(currentMonth(),payroll.getEmployee()).orElse(null);
                  return e==null;
                })
                .forEach(this::savePayRoll);

    }

    Payroll generatePayRoll(Employee employee){
        String previousMonth=getPreviousMonth();
       Optional<Payroll>previousMonthPayRollDetails= payrollRepository.getPayPeriodDetails(previousMonth,employee);
        if(previousMonthPayRollDetails.isPresent()) {
            Payroll previousPay= previousMonthPayRollDetails.get();
            Payroll payroll = new Payroll();
            payroll.setBasic(previousPay.getBasic());
            payroll.setEmployee(employee);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy");
            String payPeriod = simpleDateFormat.format(Calendar.getInstance().getTime());
            payroll.setPayPeriod(payPeriod);
            Date payDate = new Date();
            payroll.setPayDate(payDate);
            payroll.setHouseRentAllowance(previousPay.getHouseRentAllowance());
            payroll.setMedicalAllowance(previousPay.getMedicalAllowance());
            payroll.setOtherAllowance(previousPay.getOtherAllowance());
            Double grossSalary =previousPay.getGrossEarnings();
            payroll.setGrossEarnings(grossSalary);
            payroll.setProvidentFund(previousPay.getProvidentFund());
            payroll.setProfessionalTax(previousPay.getProfessionalTax());
            Double leaveDeduction = getLeaveDeduction(employee.getEmployeeID(), payroll.getBasic(), payroll);
            payroll.setLeaveDeduction(leaveDeduction);
            Double totalDeduction=payroll.getProfessionalTax()+payroll.getProvidentFund()+payroll.getLeaveDeduction();
            payroll.setTotalDeductions(totalDeduction);
            payroll.setTotalNetPayable(grossSalary - payroll.getTotalDeductions());
            return payroll;
        }
        return null;
    }

    private Double getLeaveDeduction(String employeeID,Double basicSalary,Payroll payroll) {
        LocalDate date = LocalDate.now();
        java.sql.Date currentDate = java.sql.Date.valueOf(date);
        LocalDate firstDateOfMonth = date.withDayOfMonth(1);
        java.sql.Date firstDay = java.sql.Date.valueOf(firstDateOfMonth);
        Integer totalLeaves= attendanceRepository.getNoOfAbsence(employeeID, firstDay,currentDate );
        YearMonth currentYearMonth = YearMonth.now();
        Integer numberOfDaysInMonth = currentYearMonth.lengthOfMonth();
//        YearMonth december2023 = YearMonth.of(2024, Month.JANUARY);
//        int numberOfDaysInMonth = december2023.lengthOfMonth();
        if(totalLeaves==0){
            payroll.setTotalPaidDays(numberOfDaysInMonth-totalLeaves);
            payroll.setTotalLopDays(totalLeaves);
            return 0.0;
        }

        payroll.setTotalPaidDays(numberOfDaysInMonth-totalLeaves);
        payroll.setTotalLopDays(totalLeaves);
        Double payPerDay=basicSalary/numberOfDaysInMonth;
        return  Math.round(payPerDay * totalLeaves * 100.0) / 100.0;
    }
    private String getPreviousMonth(){
        LocalDate currentDate = LocalDate.now();
        LocalDate previousMonth = currentDate.minusMonths(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return previousMonth.format(formatter);

    }
    private String currentMonth(){
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return currentDate.format(formatter);
    }
    void savePayRoll(Payroll payroll){
     if(payroll!=null)
        payrollRepository.save(payroll);
    }
}
