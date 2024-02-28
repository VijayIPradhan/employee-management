package com.employee.management.service.impl;

import com.employee.management.DTO.*;
import com.employee.management.converters.DateTimeConverter;
import com.employee.management.converters.Mapper;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.models.*;
import com.employee.management.repository.*;
import com.employee.management.service.AdminService;
import com.employee.management.service.EmailSenderService;
import com.employee.management.util.CtcCalculator;
import jakarta.mail.MessagingException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    @Autowired
    DateTimeConverter dateTimeConverter;
    @Autowired
    HikeRepository hikeRepository;


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

    @Override
    public HikeEntityDTO updateHikeDetails(HikeUpdateRequest request){
        Employee employee=employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(()->new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));
        Employee approvedBy=employeeRepository.findById(request.getApprovedBy())
                .orElseThrow(()->new CompanyException(ResCodes.EMPLOYEE_NOT_FOUND));
        HikeEntity hike=hikeRepository.findByEmployee(employee).get();
        if(!hike.getStatus()) {
            hike.setStatus(true);
            hike.setHikePercentage(Double.valueOf(request.getPercentage()));
            hike.setApprovedBy(approvedBy);
            hike.setNewSalary((hike.getPrevSalary() * (hike.getHikePercentage() / 100)) + hike.getPrevSalary());
            hike.setApprovedDate(new Date());
            hike.setEffectiveDate(dateTimeConverter.stringToLocalDateTimeConverter(request.getEffectiveDate()));
            hike.setReason(request.getReason());
            HikeEntity savedHike = hikeRepository.save(hike);
            try{
                sendHikeLetterMail(fillHikeLetter(mapper.convertToEmployeeDTO(employee),hike),employee.getEmail());
            }catch (Exception e){
                throw new RuntimeException("Something went wrong");
            }
            return mapper.convertToHikeEntityDto(savedHike);
        }
        throw new CompanyException(ResCodes.HIKE_APPROVED_ALREADY);
    }
    private byte[] fillHikeLetter(EmployeeDTO employee,HikeEntity hike) throws JRException, IOException {
        JasperReport template1 = JasperCompileManager.compileReport(new ClassPathResource("templates/hikeLetterPages/hike-letter.jrxml").getInputStream());
        JasperReport template2 = JasperCompileManager.compileReport(new ClassPathResource("templates/hikeLetterPages/hike-letter-page-two.jrxml").getInputStream());
        System.err.println("compiled ");
        Map<String, Object> parameters1 = new HashMap<>();
        parameters1.put("employee", employee);
        parameters1.put("hikeDetails",mapper.convertToHikeEntityDto(hike));
        System.out.println(mapper.convertToHikeEntityDto(hike));
        parameters1.put("hikeAmount",(hike.getNewSalary()-hike.getPrevSalary()));


        CtcCalculator calculator=new CtcCalculator();
        Map<String, Object> parameters2 = new HashMap<>();
        parameters2.put("employee", employee);
        parameters2.put("prevSalaryDetails",calculator.compensationDetails(hike.getPrevSalary()));
        System.out.println(calculator.compensationDetails(hike.getPrevSalary()));
        parameters2.put("newSalaryDetails",calculator.compensationDetails(hike.getNewSalary()));

        JasperPrint jasperPrint1 = JasperFillManager.fillReport(template1, parameters1, new JREmptyDataSource());
        JasperPrint jasperPrint2 = JasperFillManager.fillReport(template2, parameters2, new JREmptyDataSource());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        List<JasperPrint> jasperPrints = new ArrayList<>();
        jasperPrints.add(jasperPrint1);
        jasperPrints.add(jasperPrint2);
        exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrints));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();

        return outputStream.toByteArray();
    }
    private void sendHikeLetterMail(byte [] pdf,String to) throws MessagingException, IOException {
        emailSenderService.sendEmailWithAttachment(to,"Salary Hike Updation ","Update",pdf);
    }



}
