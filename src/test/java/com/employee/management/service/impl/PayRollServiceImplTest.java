package com.employee.management.service.impl;

import com.employee.management.DTO.EmployeeDTO;
import com.employee.management.DTO.PaySlip;
import com.employee.management.DTO.PayrollDTO;
import com.employee.management.converters.Mapper;
import com.employee.management.exception.CompanyException;
import com.employee.management.models.Employee;
import com.employee.management.models.Payroll;
import com.employee.management.repository.EmployeeRepository;
import com.employee.management.repository.PayrollRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PayRollServiceImplTest {

    @Mock
    PayrollRepository payrollRepository;

    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    Mapper mapper;

    @InjectMocks
    PayRollServiceImpl payRollService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPaySlip() {

        String empId = "1";
        String payPeriod = "2023-04";
        Employee employee = new Employee();
        employee.setEmployeeID(empId);
        Payroll payroll = new Payroll();
        PaySlip expectedPaySlip = new PaySlip();
        EmployeeDTO employeeDTO = new EmployeeDTO();
        PayrollDTO payrollDTO = new PayrollDTO();

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(payrollRepository.getPayPeriodDetails(payPeriod, employee)).thenReturn(Optional.of(payroll));

        when(mapper.convertToEmployeeDTO(employee)).thenReturn(employeeDTO);
        when(mapper.convertToPayRollDTO(payroll)).thenReturn(payrollDTO);

        PaySlip result = payRollService.getPaySlip(empId, payPeriod);

        assertEquals(employeeDTO, result.getEmployeeDTO());
        assertEquals(payrollDTO, result.getPayrollDTO());

        verify(employeeRepository, times(1)).findById(empId);
        verify(payrollRepository, times(1)).getPayPeriodDetails(payPeriod, employee);
        verify(mapper, times(1)).convertToEmployeeDTO(employee);
        verify(mapper, times(1)).convertToPayRollDTO(payroll);
    }

    @Test
    void testGetPaySlip_throwsEmployeeNotFound() {
        String empId = "1";
        String payPeriod = "2023-04";
        Employee employee = new Employee();
        employee.setEmployeeID(empId);
        when(employeeRepository.findById(empId)).thenReturn(Optional.empty());

        assertThrows(CompanyException.class,() ->payRollService.getPaySlip(empId,payPeriod));
        verify(employeeRepository,times(1)).findById(empId);
    }
    @Test
    void testGetPaySlip_throwsSalarySlipExists() {
        String empId = "1";
        String payPeriod = "2023-04";
        Employee employee = new Employee();
        employee.setEmployeeID(empId);
        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(payrollRepository.getPayPeriodDetails(payPeriod, employee)).thenReturn(Optional.empty());


        assertThrows(CompanyException.class,() ->payRollService.getPaySlip(empId,payPeriod));
        verify(employeeRepository,times(1)).findById(empId);
    }
}
