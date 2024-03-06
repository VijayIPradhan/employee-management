package com.employee.management.controller;

import com.employee.management.DTO.EmployeeDTO;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.service.EmployeeService;
import com.employee.management.util.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployeeControllerTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetEmployeeAuthorized() {
        String jwtToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        String empId = "123";
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeID(empId);

        when(jwtService.extractUsername(anyString())).thenReturn(empId);
        when(employeeService.getEmployee(empId)).thenReturn(employeeDTO);

        ResponseEntity<EmployeeDTO> responseEntity = employeeController.getEmployee(empId, jwtToken);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(empId, responseEntity.getBody().getEmployeeID());
        verify(jwtService, times(1)).extractUsername(anyString());
        verify(employeeService, times(1)).getEmployee(empId);
    }

    @Test
    public void testGetEmployeeUnauthorized() {
        String jwtToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        String empId = "123";
        String otherEmpId = "456";

        when(jwtService.extractUsername(anyString())).thenReturn(otherEmpId);

        CompanyException exception = assertThrows(CompanyException.class,
                () -> employeeController.getEmployee(empId, jwtToken));


        assertEquals(ResCodes.NOT_AUTHORIZED, exception.getResCodes());
        verify(jwtService, times(1)).extractUsername(anyString());
        verify(employeeService, never()).getEmployee(empId);
    }
}
