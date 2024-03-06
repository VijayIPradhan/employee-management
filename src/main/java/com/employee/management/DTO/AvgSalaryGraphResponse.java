package com.employee.management.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AvgSalaryGraphResponse {
    private String month;
    private String averageSalary;
}
