package com.employee.management.service.impl;

import com.employee.management.converters.DateTimeConverter;
import com.employee.management.repository.AttendanceRepository;
import com.employee.management.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.time.LocalDate;

class AttendanceServiceImplTest {

    @Mock
    AttendanceRepository attendanceRepository;

    @Mock
    EmployeeRepository employeeRepository;

    @Mock
    DateTimeConverter dateTimeConverter;

    @InjectMocks
    AttendanceServiceImpl attendanceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAttendance() {

        LocalDate currentDate = LocalDate.now();
        java.sql.Date currentSqlDate = java.sql.Date.valueOf(currentDate);
        LocalDate firstDateOfMonth = currentDate.withDayOfMonth(1);
        java.sql.Date firstSqlDate = Date.valueOf(firstDateOfMonth);
        int expectedAttendance = 5;

        when(attendanceRepository.getNoOfAbsence("S2C1", firstSqlDate, currentSqlDate)).thenReturn(expectedAttendance);

        int result = attendanceService.getAttendance();

        assertEquals(expectedAttendance, result);

        verify(attendanceRepository, times(1)).getNoOfAbsence("S2C1", firstSqlDate, currentSqlDate);
    }
}
