package com.employee.management.service;

import com.employee.management.DTO.*;

import java.util.List;

public interface AdminService {
    EmployeeDTO addNewEmployee(EmployeeDTO employeeDTO);

    AdminDashBoardData loadData();

    List<EmployeeDTO> fetchAllActiveEmployees();

    EmployeeDTO editEmployee(String empId,EmployeeDTO employeeDTO);

    String changeEmployeeStatus(String empId, String empStatus);

    PayrollDTO addPayroll(PayrollDTO payrollDTO,String empId);

    List<AvgSalaryGraphResponse> getSalaryGraphDataForPastSixMonths();
    String updatePfDetails(PfNumberUpdateRequest request);

    HikeEntityDTO updateHikeDetails(HikeUpdateRequest request);
}
