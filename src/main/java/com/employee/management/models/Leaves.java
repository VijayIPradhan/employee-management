package com.employee.management.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
@Data
@Entity
@Table(name = "Leaves")
public class Leaves {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "LeaveType")
    private String leaveType;

    @Column(name = "LeaveStartDate")
    private Date leaveStartDate;

    @Column(name = "LeaveEndDate")
    private Date leaveEndDate;

    @Column(name = "Status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "EmployeeID", referencedColumnName = "employeeID")
    private Employee employee;

}


