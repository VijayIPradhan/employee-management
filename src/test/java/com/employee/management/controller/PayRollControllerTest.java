package com.employee.management.controller;

import com.employee.management.DTO.EmployeeDTO;
import com.employee.management.DTO.PaySlip;
import com.employee.management.DTO.PayrollDTO;
import com.employee.management.converters.AmountToWordsConverter;
import com.employee.management.converters.PDFGeneratorForPaySlip;
import com.employee.management.filter.JWTAuthFilter;
import com.employee.management.service.EmailSenderService;
import com.employee.management.service.PDFService;
import com.employee.management.service.PayRollService;
import com.employee.management.util.JWTService;
import net.sf.jasperreports.engine.JRException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PayRollController.class)
class PayRollControllerTest {
    @InjectMocks
    PayRollController payRollController;
    @MockBean
    PayRollService payRollService;
    @MockBean
    JWTService jwtService;
    @MockBean
    JWTAuthFilter jwtFilter;
    @MockBean
    AmountToWordsConverter converter;
    @MockBean
    EmailSenderService email;
    @MockBean
    PDFGeneratorForPaySlip generator;
    @MockBean
    PDFService pdfService;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    WebApplicationContext context;

    @BeforeEach
    void setUp() {
        mockMvc= MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void testGetPaySlip() throws Exception {
        PaySlip paySlip=new PaySlip();
        paySlip.setEmployeeDTO(new EmployeeDTO());
        paySlip.setPayrollDTO(new PayrollDTO());
        String empId= "S2C1";
        String payPeriod= "January 2024";
        when(payRollService.getPaySlip(any(),any())).thenReturn(paySlip);
        mockMvc.perform(get("/salary/get")
                        .param("employeeId", empId)
                        .param("payPeriod", payPeriod))
                .andExpect(status().isOk());
    }

    @Test
    void testDownloadPaySlipWhenPaySlipNull() throws Exception {
        String empId = "123";
        String payPeriod = "2023-04";
        byte[] pdfBytes = new byte[0];
        PaySlip paySlip=new PaySlip();
        paySlip.setEmployeeDTO(new EmployeeDTO());
        paySlip.setPayrollDTO(new PayrollDTO());
        when(generator.generatePaySlipPdf(any(), anyString())).thenReturn(pdfBytes);

        mockMvc.perform(get("/salary/download")
                        .param("employeeId", empId)
                        .param("payPeriod", payPeriod))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testDownloadPaySlip() throws Exception {
        String empId = "123";
        String payPeriod = "Feb 2023";
        PaySlip paySlip = new PaySlip();
        paySlip.setEmployeeDTO(new EmployeeDTO());
        paySlip.setPayrollDTO(new PayrollDTO());
        when(payRollService.getPaySlip(empId, payPeriod)).thenReturn(paySlip);


        mockMvc.perform(get("/salary/download")
                        .param("employeeId", empId)
                        .param("payPeriod", payPeriod)
                        .contentType("application/json"))
                .andExpect(status().isOk());
    }


    @Test
    void testPreviewPaySlip() throws Exception {
        String empId = "123";
        String payPeriod = "Feb 2023";
        byte[] pdfBytes = new byte[0];
        PaySlip paySlip = new PaySlip();
        paySlip.setEmployeeDTO(new EmployeeDTO());
        paySlip.setPayrollDTO(new PayrollDTO());
        when(pdfService.generatePaySlipResponseEntity(empId, payPeriod, true)).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));


        mockMvc.perform(get("/salary/preview-salary")
                        .param("employeeId", empId)
                        .param("payPeriod", payPeriod)
                        .contentType(MediaType.APPLICATION_PDF))
                .andExpect(status().isOk());
    }
}