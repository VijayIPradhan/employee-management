package com.employee.management.service.impl;

import com.employee.management.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.models.Employee;
import com.employee.management.DTO.EmployeeDetails;

import java.util.Optional;
@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {

    @Mock
    EmployeeRepository employeeRepository;

    @InjectMocks
    UserInfoService userInfoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_Success() {

        String username = "testUser";
        Employee employee = new Employee();
        employee.setEmployeeID(username);
        EmployeeDetails expectedUserDetails = new EmployeeDetails(employee);

        when(employeeRepository.findById(username)).thenReturn(Optional.of(employee));

        UserDetails result = userInfoService.loadUserByUsername(username);

        assertEquals(expectedUserDetails, result);

        verify(employeeRepository, times(1)).findById(username);
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        String username = "testUser";

        when(employeeRepository.findById(username)).thenReturn(Optional.empty());

        assertThrows(CompanyException.class, () -> userInfoService.loadUserByUsername(username));

        verify(employeeRepository, times(1)).findById(username);
    }
}
