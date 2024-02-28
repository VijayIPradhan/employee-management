package com.employee.management.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvgSalaryGraphResponse {
    private String month;
    private String averageSalary;
}
