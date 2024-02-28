package com.employee.management.controller.Exception;

import com.employee.management.exception.CompanyException;
import com.employee.management.exception.ErrorDetails;
import com.employee.management.exception.ResCodes;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CompanyException.class)
    public ResponseEntity<ErrorDetails>companyExceptionHandler(CompanyException companyException){
        ResCodes resCodes=companyException.getResCodes();
        ErrorDetails errorDetails=new ErrorDetails();
        if(companyException.getEmpId()!=null){
            errorDetails.setErrorDesc(resCodes.getErrorMsg()+"--"+companyException.getEmpId());
        }else{
            errorDetails.setErrorDesc(resCodes.getErrorMsg());
        }
        errorDetails.setErrorCode(resCodes.getErrorCode());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email address must be unique");
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage()+"-You are not authorized to access this endpoint");
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Token expired: " + ex.getMessage());
    }
}
