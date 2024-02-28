package com.employee.management.DTO;

import lombok.Data;

import java.sql.Date;

@Data
public class OfferLetterDTO {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String designation;
    private String department;
    private String issuedDate;
    private String joiningDate;
    private String ctc;
}
