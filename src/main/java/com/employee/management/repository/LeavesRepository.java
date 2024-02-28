package com.employee.management.repository;

import com.employee.management.models.Leaves;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeavesRepository extends JpaRepository<Leaves,Long> {
}
