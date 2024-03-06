package com.employee.management.service.impl;

import com.employee.management.DTO.ChangePasswordRequest;
import com.employee.management.DTO.EmployeeDTO;
import com.employee.management.DTO.ForgetPasswordRequest;
import com.employee.management.converters.Mapper;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.models.Employee;
import com.employee.management.models.OtpEntity;
import com.employee.management.models.Status;
import com.employee.management.repository.EmployeeRepository;
import com.employee.management.repository.OtpRepository;
import com.employee.management.service.EmailSenderService;
import com.employee.management.util.PasswordGenerator;
import com.employee.management.util.Verification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.employee.management.service.impl.AdminServiceImplTest.getEmployee;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {

    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    Mapper mapper;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailSenderService emailSenderService;
    @Mock
    private OtpRepository otpRepository;
    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private Verification verification;
    @InjectMocks
    private EmployeeServiceImpl employeeService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetEmployee() {
        Employee employee = new Employee();
        employee.setEmployeeID("1");
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeID("1");

        when(employeeRepository.findById("1")).thenReturn(Optional.of(employee));
        when(mapper.convertToEmployeeDTO(employee)).thenReturn(employeeDTO);

        EmployeeDTO result = employeeService.getEmployee("1");

        assertEquals(employeeDTO, result);
        verify(employeeRepository, times(1)).findById("1");
        verify(mapper, times(1)).convertToEmployeeDTO(employee);
    }

    @Test
    void testGetEmployee_throwsException() {
        when(employeeRepository.findById("1")).thenReturn(Optional.empty());
        assertThrows(CompanyException.class, () -> employeeService.getEmployee("1"));
        verify(employeeRepository, times(1)).findById("1");
    }

    @Test
    void testChangePassword() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setEmployeeId("1");
        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword");

        Authentication authenticate = mock(Authentication.class);
        when(authenticate.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authenticate);

        Employee employee = new Employee();
        employee.setEmployeeID("1");
        employee.setPassword("newPassword");

        when(employeeRepository.findById("1")).thenReturn(Optional.of(employee));

        String result = employeeService.changePassword(request);

        assertEquals("Success", result);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(employeeRepository, times(1)).findById("1");
    }

    @Test
    void testChangePassword_throwsExceptionEmployeeNotFound() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setEmployeeId("1");
        request.setOldPassword("<PASSWORD>");
        request.setNewPassword("<new>");
        Authentication authenticate = mock(Authentication.class);
        when(authenticate.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authenticate);

        Employee employee =  getEmployee();
        employee.setEmployeeID("2");
        employee.setPassword("<new>");

        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(CompanyException.class, () -> employeeService.changePassword(request));
        verify(employeeRepository, times(1)).findById("1");
    }
@Test
    void testChangePassword_throwsExceptionElsePart() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setEmployeeId("1");
        request.setOldPassword("<PASSWORD>");
        request.setNewPassword("<new>");
        Authentication authenticate = mock(Authentication.class);
        when(authenticate.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authenticate);

        assertThrows(CompanyException.class, () -> employeeService.changePassword(request));
        verify(authenticationManager,times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
    @Test
    void testChangePassword_catchBlock() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setEmployeeId("1");
        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword");

        Authentication authenticate = mock(Authentication.class);
        when(authenticate.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(BadCredentialsException.class);

        assertThrows(CompanyException.class, () -> employeeService.changePassword(request));
        verify(authenticationManager,times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));

    }
    @Test
    void testResetPasswordMail() {
        Employee employee = new Employee();
        employee.setEmployeeID("1");
        employee.setEmail("test@example.com");
        OtpEntity otpEntity =new OtpEntity();
        otpEntity.setEmployee(employee);
        otpEntity.setOtpValue("12345");

        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(passwordGenerator.generatePassword(anyInt())).thenReturn("12345");
        when(otpRepository.findByEmployee(employee)).thenReturn(Optional.empty());
        when(otpRepository.save(any(OtpEntity.class))).thenReturn(otpEntity);
        String result = employeeService.resetPasswordMail("1");

        assertEquals("Success", result);
        verify(employeeRepository, times(1)).findById("1");
        verify(otpRepository, times(1)).findByEmployee(employee);
    }

    @Test
    void testResetPasswordMail_throwsException() {
        Employee employee = new Employee();
        employee.setEmployeeID("1");
        employee.setEmail("test@example.com");

        when(employeeRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(CompanyException.class, () -> employeeService.resetPasswordMail("1"));
        verify(employeeRepository, times(1)).findById("1");
    }
    @Test
    void testResetPasswordMail_PreviousOtp() {

        String empId = "1";
        Employee employee = new Employee();
        employee.setEmployeeID(empId);
        OtpEntity prevOtpEntity = new OtpEntity();
        prevOtpEntity.setEmployee(employee);

        when(employeeRepository.findById(empId)).thenReturn(Optional.of(employee));
        when(otpRepository.findByEmployee(employee)).thenReturn(Optional.of(prevOtpEntity));

        String result = employeeService.resetPasswordMail(empId);

        verify(otpRepository, times(1)).findByEmployee(employee);

        verify(otpRepository, times(1)).delete(prevOtpEntity);

        assertEquals("Success", result);
    }


    @Test
    void testForgetPassword() {
        ForgetPasswordRequest request = new ForgetPasswordRequest();
        request.setEmpId("1");
        request.setOtp("12345");
        request.setNewPassword("<PASSWORD>");

        Employee employee = new Employee();
        employee.setEmployeeID("1");
        employee.setPassword("<PASSWORD>");
        OtpEntity otp = new OtpEntity();
        otp.setEmployee(employee);
        otp.setOtpValue("12345");

        when(verification.verifyOtp(request.getEmpId(),request.getOtp())).thenReturn(true);
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn(employee.getPassword());
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(otpRepository.findByEmployee(employee)).thenReturn(Optional.of(otp));
        when(otpRepository.save(any(OtpEntity.class))).thenReturn(otp);

        String result = employeeService.forgetPassword(request);

        assertEquals("Success", result);
        verify(employeeRepository, times(1)).findById("1");
    }
    @Test
    void testForgetPassword_throwsException() {
        ForgetPasswordRequest request = new ForgetPasswordRequest();
        request.setEmpId("1");
        request.setOtp("12345");
        request.setNewPassword("<PASSWORD>");

        when(verification.verifyOtp(request.getEmpId(),request.getOtp())).thenReturn(true);
        when(employeeRepository.findById(request.getEmpId())).thenReturn(Optional.empty());

        assertThrows(CompanyException.class, () -> employeeService.forgetPassword(request));
        verify(employeeRepository,times(1)).findById(anyString());
    }
    @Test
    void testForgetPassword_OtpNotVerified() {
        ForgetPasswordRequest request = new ForgetPasswordRequest();
        request.setEmpId("1");
        request.setOtp("12345");
        request.setNewPassword("<PASSWORD>");

        when(verification.verifyOtp(request.getEmpId(),request.getOtp())).thenReturn(false);

        assertThrows(CompanyException.class, () -> employeeService.forgetPassword(request));
        verify(verification,times(1)).verifyOtp(request.getEmpId(),request.getOtp());
    }
}
