package com.employee.management.repository;

import com.employee.management.models.Employee;
import com.employee.management.models.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpEntity,Long> {
   Optional<OtpEntity>  findByEmployee(Employee employee);
}
