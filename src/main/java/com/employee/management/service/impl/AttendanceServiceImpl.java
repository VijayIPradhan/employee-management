package com.employee.management.service.impl;

import com.employee.management.converters.DateTimeConverter;
import com.employee.management.repository.AttendanceRepository;
import com.employee.management.repository.EmployeeRepository;
import com.employee.management.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    @Autowired
    AttendanceRepository attendanceRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    DateTimeConverter dateTimeConverter;

    @Override
    public int getAttendance() {
        LocalDate date = LocalDate.now();
        java.sql.Date cur = java.sql.Date.valueOf(date);
        LocalDate firstDateOfMonth = date.withDayOfMonth(1);
        java.sql.Date fir = Date.valueOf(firstDateOfMonth);
        return attendanceRepository.getNoOfAbsence("S2C1", fir, cur);
    }
}


