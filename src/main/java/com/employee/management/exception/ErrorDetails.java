package com.employee.management.exception;

import lombok.Data;

@Data
public class ErrorDetails {
    private String errorCode;
    private String errorDesc;
}
