package com.employee.management.repository;

import com.employee.management.models.Attendance;
import com.employee.management.models.Employee;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    @Query("SELECT COUNT(a) FROM Attendance a " +
            "WHERE a.employee.id = :id " +
            "AND a.date BETWEEN :startDate AND :endDate " +
            "AND a.status = 'UNPAID LEAVE'")
    Integer getNoOfAbsence(String id, Date startDate, Date endDate);

}
