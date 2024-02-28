package com.employee.management.repository;

import com.employee.management.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

    @Query("SELECT e FROM Employee e JOIN e.roles r WHERE r.name = 'ROLE_ADMIN'")
    List<Employee> findAdminEmployees();
    @Query("SELECT e FROM Employee e JOIN e.status s WHERE s.name = :statusName")
    List<Employee> findByStatusName(String statusName);

}
