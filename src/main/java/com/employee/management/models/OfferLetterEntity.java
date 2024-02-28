package com.employee.management.models;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Entity
@Table(name="OFFER_LETTER")
public class OfferLetterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long offerLetterId;

    @Column(name="full_name")
    private String fullName;

    @Column(name="email",unique = true)
    private String email;

    @Column(name="phone_number")
    private String phoneNumber;

    @Column(name="designation")
    private String designation;

    @Column(name="department")
    private String department;

    @Column(name = "issued_date")
    private Date issuedDate;

    @Column(name = "joining_date")
    private Date joiningDate;

    @Column(name="package")
    private Double ctc;
}
