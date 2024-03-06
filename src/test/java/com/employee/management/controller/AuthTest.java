package com.employee.management.controller;

import com.employee.management.DTO.AuthRequest;
import com.employee.management.DTO.ChangePasswordRequest;
import com.employee.management.DTO.ForgetPasswordRequest;
import com.employee.management.controller.Auth;
import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ResCodes;
import com.employee.management.service.EmployeeService;
import com.employee.management.service.impl.UserInfoService;
import com.employee.management.util.JWTService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import javax.swing.text.BadLocationException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(Auth.class)
class AuthTest {
    @InjectMocks
    private Auth auth;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JWTService jwtService;

    @MockBean
    UserInfoService userInfoService;

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockBean
    private EmployeeService employeeService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private EmployeeController employeeController;

    @Mock
    Authentication authentication;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void testLogin_Successful() throws Exception {
        // Given
        String requestBody = "{\"empId\":\"S2C1\",\"password\":\"password123\"}";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("S2C1", requestBody, new ArrayList<>()));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }


    @Test
    void testLogin_InvalidCredentials() throws Exception {
        String requestBody = "{\"empId\":\"invalidId\",\"password\":\"invalidPassword\"}";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("S2C1", requestBody));
        when(authentication.isAuthenticated()).thenReturn(false);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(result -> assertInstanceOf(CompanyException.class, result.getResolvedException()));
    }


    @Test
    void testLogin_SuccessfulAuthentication() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("S2C1", null, new ArrayList<>()));

        when(jwtService.generateToken(anyString())).thenReturn("mocked.jwt.token");

        String requestBody = "{\"empId\":\"S2C1\",\"password\":\"password123\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("mocked.jwt.token")); // Asserting the generated JWT token
    }


    @Test
    void testLogin_EmptyFields() throws Exception {
        String requestBody = "{\"empId\":\"\",\"password\":\"\"}";
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(AuthenticationServiceException.class);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(result -> assertInstanceOf(CompanyException.class, result.getResolvedException()));
    }

    @Test
    void testChangePassword() throws Exception {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("validEmpId", "oldPassword", "newPassword");
        given(employeeService.changePassword(changePasswordRequest)).willReturn("Password changed successfully");

        mockMvc.perform(post("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"empId\":\"validEmpId\",\"oldPassword\":\"oldPassword\",\"newPassword\":\"newPassword\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testSendResetMail() throws Exception {
        given(employeeService.resetPasswordMail("validEmpId")).willReturn("Reset mail sent successfully");

        mockMvc.perform(MockMvcRequestBuilders.get("/auth/reset-mail/validEmpId"))
                .andExpect(status().isOk());
    }

    @Test
    void testSendForgetPasswordMail() throws Exception {
        ForgetPasswordRequest forgetPasswordRequest = new ForgetPasswordRequest("validEmpId", "otp","newPassword");
        given(employeeService.forgetPassword(forgetPasswordRequest)).willReturn("Forget password mail sent successfully");

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"empId\":\"validEmpId\",\"email\":\"validEmail\"}"))
                .andExpect(status().isOk());
    }

}
