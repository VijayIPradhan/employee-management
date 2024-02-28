package com.employee.management.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "Employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "com.employee.management.util.CustomIdGenerator")
    @Column(name = "EmployeeID")
    private String employeeID;

    @Column(name = "EmployeeName")
    private String employeeName;

    @Column(name = "Designation")
    private String designation;

    @Column(name = "Location")
    private String location;

    @Column(name = "BankName")
    private String bankName;

    @Column(name = "AccountNo")
    private String accountNo;

    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(
            name = "Employee_Role",
            joinColumns = @JoinColumn(name = "EmployeeID"),
            inverseJoinColumns = @JoinColumn(name = "RoleID")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "StatusID", referencedColumnName = "StatusID")
    private Status status;

    @Column(name = "DateOfJoin")
    private Date dateOfJoin;

    @Column(name = "Password")
    private String password;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "uanNumber")
    private String uanNumber;
    @Column(name = "pfNumber")
    private String pfNumber;

    @Override
    public String toString() {
        return "Employee{" +
                "employeeID='" + employeeID + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", designation='" + designation + '\'' +
                ", location='" + location + '\'' +
                ", bankName='" + bankName + '\'' +
                ", accountNo='" + accountNo + '\'' +
                ", status=" + status +
                ", dateOfJoin=" + dateOfJoin +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", uanNumber='" + uanNumber + '\'' +
                ", pfNumber='" + pfNumber + '\'' +
                '}';
    }
}

