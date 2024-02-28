package com.employee.management.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name="HIKE_TABLE")
public class HikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="employee_id", referencedColumnName="EmployeeID")
    private Employee employee;

    @Column(name="prev-salary")
    private Double prevSalary;

    @Column(name="new-salary")
    private Double newSalary;

    @Column(name="hike-percentage")
    private Double hikePercentage;

    @Column(name="reason")
    private String reason;

    @OneToOne
    @JoinColumn(name="approved_by", referencedColumnName="EmployeeID")
    private Employee approvedBy;

    @Column(name="approved_date")
    private Date approvedDate;

    @Column(name="status")
    private Boolean status;

    @Column(name="effective_date")
    private Date effectiveDate;


}
