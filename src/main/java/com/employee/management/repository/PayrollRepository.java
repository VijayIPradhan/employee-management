package com.employee.management.repository;

import com.employee.management.models.Employee;
import com.employee.management.models.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll,Long> {
   Optional<Payroll> findByEmployee(Employee employee);
   @Query("select a from Payroll a where a.payPeriod=:payPeriod and a.employee=:employee ")
   Optional<Payroll> getPayPeriodDetails(String payPeriod,Employee employee);
   @Query("select a from Payroll a where a.payPeriod=:payPeriod ")
   Optional<List<Payroll>>getPayDetails(String payPeriod);
   @Query(value = "select * from Payroll WHERE STR_TO_DATE(CONCAT('01 ',pay_period), '%d %M %Y') " +
           "BETWEEN :fromDate AND DATE_ADD(:fromDate, INTERVAL 5 MONTH)", nativeQuery = true)
   List<Payroll> findByPayPeriodRange(String fromDate);

}
