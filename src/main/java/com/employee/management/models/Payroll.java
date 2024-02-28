package com.employee.management.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "Payroll")
public class Payroll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "PayPeriod")
    private String payPeriod;

    @Column(name = "PayDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date payDate;

    @ManyToOne
    @JoinColumn(name = "EmployeeID", referencedColumnName = "employeeID")
    private Employee employee;

    @Column(name = "Basic")
    private Double basic;

    @Column(name = "HouseRentAllowance")
    private Double houseRentAllowance;

    @Column(name = "MedicalAllowance")
    private Double medicalAllowance;

    @Column(name = "OtherAllowance")
    private Double otherAllowance;

    @Column(name = "GrossEarnings")
    private Double grossEarnings;

    @Column(name = "ProvidentFund")
    private Double providentFund;

    @Column(name="LeaveDeduction")
    private Double leaveDeduction;

    @Column(name="ProfessionalTax")
    private Double professionalTax;

    @Column(name="IncomeTax")
    private Double incomeTax;

    @Column(name = "TotalDeductions")
    private Double totalDeductions;

    @Column(name = "TotalNetPayable")
    private Double totalNetPayable;

    @Column(name="TotalPaidDays")
    private Integer totalPaidDays;

    @Column(name="TotalLopDays")
    private Integer totalLopDays;
}

