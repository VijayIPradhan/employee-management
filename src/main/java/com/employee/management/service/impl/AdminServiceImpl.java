package com.employee.management.service.impl;

import com.employee.management.DTO.*;
import com.employee.management.converters.Mapper;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.models.Employee;
import com.employee.management.models.Payroll;
import com.employee.management.models.Role;
import com.employee.management.models.Status;
import com.employee.management.repository.EmployeeRepository;
import com.employee.management.repository.PayrollRepository;
import com.employee.management.repository.RoleRepository;
import com.employee.management.repository.StatusRepository;
import com.employee.management.service.AdminService;
import com.employee.management.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PayrollRepository payrollRepository;
    @Autowired
    StatusRepository statusRepository;
    @Autowired
    Mapper mapper;
    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    PasswordEncoder passwordEncoder;


    private String getTodayDateFormatted(){
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return today.format(formatter);
    }
    private int getEmployeeCount(){
        List<Employee>employees=employeeRepository.findAll();
        return employees.size();
    }
    @Override
    public EmployeeDTO addNewEmployee(EmployeeDTO employeeDTO){
        Employee employee=mapper.convertToEmployeeEntity(employeeDTO);
        Role role=roleRepository.findById(2L).get();
        String password=employee.getPassword();
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employee.getRoles().add(role);
        employee.setStatus(statusRepository.findById(1L).get());
        Employee savedEmployee = employeeRepository.save(employee);
        emailSenderService.sendSimpleEmail(employee.getEmail(),"Account Created",getBodyOfMail(savedEmployee.getEmployeeName(),savedEmployee.getEmployeeID(),password));

        return mapper.convertToEmployeeDTO(savedEmployee);
    }
    private String getBodyOfMail(String name, String empId, String password) {
        StringBuilder body = new StringBuilder();
        body.append("Hi ").append(name).append(",\n\n");
        body.append("Welcome Seabed2Crest Technologies Pvt Ltd").append("\n");
        body.append("Here are your login details:").append("\n");
        body.append("Employee ID: ").append(empId).append("\n");
        body.append("Password: ").append(password).append("\n\n");
        body.append("Please keep this information confidential.").append("\n\n");
        body.append("If you have any questions, feel free to contact us.").append("\n\n");
        body.append("Best regards,\nThe HR Team");

        return body.toString();
    }


    @Override
    public AdminDashBoardData loadData(){
        AdminDashBoardData adminDashBoardData=new AdminDashBoardData();
        YearMonth currentYearMonth = YearMonth.now();
        YearMonth previousYearMonth = currentYearMonth.minusMonths(1);
        String previousMonthFormatted = previousYearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                + " " + previousYearMonth.getYear();
        List<Payroll>payrolls=payrollRepository.getPayDetails(previousMonthFormatted)
                .orElseThrow(()->new CompanyException(ResCodes.SALARY_DETAILS_NOT_FOUND));
        Double averageSalary=payrolls.stream()
                .mapToDouble(Payroll::getGrossEarnings)
                .average()
                .orElse(0.0);

        adminDashBoardData.setAverageSalary(averageSalary);
        adminDashBoardData.setTodayDate(getTodayDateFormatted());
        adminDashBoardData.setNoOfEmployees(getEmployeeCount());
        return adminDashBoardData;
    }
    @Override
    public List<EmployeeDTO> fetchAllActiveEmployees(){
        List<Employee>employees=employeeRepository.findAll();
        return  employees.stream()
                .filter(Objects::nonNull)
                .filter(employee -> employee.getStatus().getName().equals("active"))
                .map(mapper::convertToEmployeeDTO)
                .toList();
    }

    @Override
    public EmployeeDTO editEmployee(String empId,EmployeeDTO employeeDTO){
        Employee employee =employeeRepository.findById(empId)
                .orElseThrow(()-> new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));
        employee.setDesignation(employeeDTO.getDesignation());
        employee.setLocation(employeeDTO.getLocation());
        employee.setBankName(employeeDTO.getBankName());
        employee.setAccountNo(employeeDTO.getAccountNo());
        employee.setEmployeeName(employeeDTO.getEmployeeName());
        Employee savedEmployee = employeeRepository.save(employee);
        return mapper.convertToEmployeeDTO(savedEmployee);
    }

    @Override
    public String changeEmployeeStatus(String empId, String empStatus){

        Employee employee=employeeRepository.findById(empId)
                .orElseThrow(()->new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));
        Status status=statusRepository.findByName(empStatus.toLowerCase())
                .orElseThrow(()->new CompanyException(ResCodes.INVALID_STATUS));
        employee.setStatus(status);
        employeeRepository.save(employee);
        return "Employee status changed successfully";
    }

    @Override
    public PayrollDTO addPayroll(PayrollDTO payrollDTO,String empId){
      Employee employee=employeeRepository.findById(empId)
              .orElseThrow(()->new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));
      Payroll a=payrollRepository.getPayPeriodDetails(payrollDTO.getPayPeriod(),employee).orElse(null);
      if(a==null) {
          Payroll payroll = mapper.convertToPayroll(payrollDTO);
          payroll.setEmployee(employee);
          Payroll savedPayroll = payrollRepository.save(payroll);
          return mapper.convertToPayRollDTO(savedPayroll);
      }else{
          throw new CompanyException(ResCodes.DUPLICATE_PAYROLL_DETAILS);
      }
    }
    @Override
    public List<AvgSalaryGraphResponse> getSalaryGraphDataForPastSixMonths(){
        LocalDate currentDate = LocalDate.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sixMonthsAgo = currentDate.minusMonths(6);
        String sixMonthAgo = sixMonthsAgo.format(formatter);

        List<Payroll> sixMonthData=payrollRepository.findByPayPeriodRange(sixMonthAgo);

        Map<String, Double> averageSalaryByPayPeriod = sixMonthData.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Payroll::getPayPeriod,
                        Collectors.averagingDouble(Payroll::getTotalNetPayable)));

        return  averageSalaryByPayPeriod.entrySet().stream()
                .map(entry -> new AvgSalaryGraphResponse(entry.getKey(), String.format("%.2f", entry.getValue())))
                .toList();

    }

    @Override
    public String updatePfDetails(PfNumberUpdateRequest request) {
        Employee employee=employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(()->new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));
        if(request.getUanNumber()!=null && request.getPfNumber()!=null) {
            employee.setUanNumber(request.getUanNumber());
            employee.setPfNumber(request.getPfNumber());
            employeeRepository.save(employee);
            return "Successfully Updated";
        }
        throw new CompanyException(ResCodes.EMPTY_FIELDS);
    }

}
