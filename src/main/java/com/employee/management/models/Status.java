package com.employee.management.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "Status")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StatusID")
    private Long statusID;

    @Column(name = "Name")
    private String name;
}
