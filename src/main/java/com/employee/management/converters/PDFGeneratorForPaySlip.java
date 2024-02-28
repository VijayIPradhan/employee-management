package com.employee.management.converters;

import com.employee.management.DTO.PaySlip;
import net.sf.jasperreports.engine.*;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class PDFGeneratorForPaySlip {
    public byte[] generatePaySlipPdf(PaySlip paySlip, String amountInWords) throws JRException {
        InputStream template = getClass().getResourceAsStream("/templates/pay-slip.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(template);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("payroll", paySlip.getPayrollDTO());
        parameters.put("employee", paySlip.getEmployeeDTO());
        parameters.put("amount", amountInWords);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
