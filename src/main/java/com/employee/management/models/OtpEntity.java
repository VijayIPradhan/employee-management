package com.employee.management.models;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name="otp_table")
public class OtpEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "EmployeeID")
    private Employee employee;

    @Column(name = "otp_value")
    private String otpValue;

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;
}
