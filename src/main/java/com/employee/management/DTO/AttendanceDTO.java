package com.employee.management.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class AttendanceDTO {

    private Long id;
    private Long employeeId;
    private Date date;
    private String status;
}