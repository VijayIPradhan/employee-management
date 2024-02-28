package com.employee.management.DTO;

import lombok.Data;

@Data
public class PfNumberUpdateRequest {
    private String employeeId;
    private String PfNumber;
    private String UanNumber;
}
