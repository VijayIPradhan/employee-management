package com.employee.management.exception;

import lombok.Getter;

@Getter
public class CompanyException extends RuntimeException{
    ResCodes resCodes;
    String empId;
    public CompanyException(ResCodes resCode){
        super(resCode.getErrorCode()+"-"+resCode.getErrorMsg());
        this.resCodes=resCode;
    }
    public CompanyException(ResCodes resCode,String empId){
        super(resCode.getErrorCode()+"-"+resCode.getErrorMsg()+"-"+empId);
        this.resCodes=resCode;
        this.empId=empId;
    }
}
