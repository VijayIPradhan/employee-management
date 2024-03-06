package com.employee.management.controller;

import com.employee.management.DTO.CtcData;
import com.employee.management.DTO.OfferLetterDTO;
import com.employee.management.filter.JWTAuthFilter;
import com.employee.management.service.PDFService;
import com.employee.management.service.EmailSenderService;
import com.employee.management.service.OfferLetterService;
import com.employee.management.util.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OfferLetterController.class)
public class OfferLetterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OfferLetterService offerLetterService;

    @MockBean
    private EmailSenderService emailSenderService;

    @MockBean
    private PDFService pdfService;
    @MockBean
    JWTService jwtService;
    @MockBean
    JWTAuthFilter jwtFilter;
    @Autowired
    WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testIssueOfferLetter() throws Exception {
        OfferLetterDTO offerLetterDTO = new OfferLetterDTO();
        offerLetterDTO.setFullName("Vijay Pradhan");
        offerLetterDTO.setEmail("vijay@seabed2crest.com");
        offerLetterDTO.setPhoneNumber("+1234567890");
        offerLetterDTO.setDesignation("Software Engineer");
        offerLetterDTO.setDepartment("IT");
        offerLetterDTO.setJoiningDate("29-Feb-2024");
        offerLetterDTO.setCtc("2,16,000");
        when(offerLetterService.issueNewOfferLetter(any(OfferLetterDTO.class))).thenReturn(offerLetterDTO);
        when(pdfService.generateMergedOfferReport(any(OfferLetterDTO.class))).thenReturn(new byte[0]);

        mockMvc.perform(post("/offer-letter/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(offerLetterDTO)))
                .andExpect(status().isOk());

        verify(emailSenderService, times(1)).sendEmailWithAttachment(anyString(), anyString(), anyString(), any(byte[].class));
    }
    @Test
    public void testIssueOfferLetterWithException() throws Exception {
        OfferLetterDTO offerLetterDTO = new OfferLetterDTO();
        offerLetterDTO.setFullName("Vijay Pradhan");
        offerLetterDTO.setEmail("vijay@seabed2crest.com");
        offerLetterDTO.setPhoneNumber("+1234567890");
        offerLetterDTO.setDesignation("Software Engineer");
        offerLetterDTO.setDepartment("IT");
        offerLetterDTO.setJoiningDate("29-Feb-2024");
        offerLetterDTO.setCtc("2,16,000");
        when(offerLetterService.issueNewOfferLetter(any(OfferLetterDTO.class))).thenThrow(new RuntimeException("Internal Server Error"));

        mockMvc.perform(post("/offer-letter/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(offerLetterDTO)))
                .andExpect(status().isInternalServerError());

    }


    @Test
    public void testPreviewCompensationDetails() throws Exception {
        CtcData ctcData = new CtcData();
        OfferLetterDTO offerLetterDTO = new OfferLetterDTO();
        offerLetterDTO.setFullName("Vijay Pradhan");
        offerLetterDTO.setEmail("vijay@seabed2crest.com");
        offerLetterDTO.setPhoneNumber("+1234567890");
        offerLetterDTO.setDesignation("Software Engineer");
        offerLetterDTO.setDepartment("IT");
        offerLetterDTO.setJoiningDate("29-Feb-2024");
        offerLetterDTO.setCtc("2,16,000");

        when(offerLetterService.preview(any())).thenReturn(ctcData);

        mockMvc.perform(post("/offer-letter/preview-compensation-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(offerLetterDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void testPreviewLetter() throws Exception {
        OfferLetterDTO offerLetterDTO = new OfferLetterDTO();
        String testData= """
                {
                 "fullName": "John Doe",
                 "email": "john.doe@example.com",
                 "phoneNumber": "1234567890",
                 "designation": "Software Engineer",
                 "department": "Engineering",
                 "issuedDate": "2023-04-01",
                 "joiningDate": "2023-04-15",
                 "ctc": "50000"
                }
                """;
        when(pdfService.generateMergedOfferReport(any(OfferLetterDTO.class))).thenReturn(new byte[0]);

        mockMvc.perform(post("/offer-letter/preview-letter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testData))
                .andExpect(status().isOk());
    }

    @Test
    public void testPreviewLetterById() throws Exception {
        Long id = 1L;
        OfferLetterDTO offerLetterDTO = new OfferLetterDTO();
        String testData= """
                {
                 "fullName": "John Doe",
                 "email": "john.doe@example.com",
                 "phoneNumber": "1234567890",
                 "designation": "Software Engineer",
                 "department": "Engineering",
                 "issuedDate": "2023-04-01",
                 "joiningDate": "2023-04-15",
                 "ctc": "50000"
                }
                """;
        when(offerLetterService.get(anyLong())).thenReturn(offerLetterDTO);
        when(pdfService.generateMergedOfferReport(any(OfferLetterDTO.class))).thenReturn(new byte[0]);

        mockMvc.perform(MockMvcRequestBuilders.get("/offer-letter/preview-letter-by-id/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testData))
                .andExpect(status().is(200));
    }
}
