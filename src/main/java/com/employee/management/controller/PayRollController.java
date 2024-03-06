package com.employee.management.controller;

import com.employee.management.DTO.PaySlip;
import com.employee.management.converters.AmountToWordsConverter;
import com.employee.management.converters.PDFGeneratorForPaySlip;
import com.employee.management.service.EmailSenderService;
import com.employee.management.service.PDFService;
import com.employee.management.service.PayRollService;
import com.employee.management.util.Util;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/salary")
public class PayRollController {

    private final PayRollService payRollService;
    private final EmailSenderService emailSenderService;
    private final PDFService pdfService;


    @Autowired
    public PayRollController(PayRollService payRollService, EmailSenderService emailSenderService, PDFService pdfService) {
        this.payRollService = payRollService;
        this.emailSenderService = emailSenderService;
        this.pdfService = pdfService;
    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<PaySlip> getPaySlip(@RequestParam("employeeId") String empId, @RequestParam("payPeriod") String payPeriod) {
        return new ResponseEntity<>(payRollService.getPaySlip(empId, payPeriod), HttpStatus.OK);
    }

    @GetMapping("/download")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<byte[]> getPaySlipDownload(@RequestParam("employeeId") String empId, @RequestParam("payPeriod") String payPeriod) {
        PaySlip paySlip = payRollService.getPaySlip(empId, payPeriod);
        if (paySlip == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return pdfService.generatePaySlipResponseEntity(empId, payPeriod, true);
    }

    @GetMapping("/preview-salary")
    public ResponseEntity<byte[]> previewPaySlip(@RequestParam("employeeId") String empId, @RequestParam("payPeriod") String payPeriod) {
        return pdfService.generatePaySlipResponseEntity(empId, payPeriod, false);
    }


}
