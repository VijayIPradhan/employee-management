package com.employee.management.service;

import com.employee.management.DTO.CtcData;
import com.employee.management.DTO.EmployeeDTO;
import com.employee.management.DTO.OfferLetterDTO;
import com.employee.management.DTO.PaySlip;
import com.employee.management.converters.AmountToWordsConverter;
import com.employee.management.converters.Mapper;
import com.employee.management.converters.PDFGeneratorForPaySlip;
import com.employee.management.models.HikeEntity;
import com.employee.management.util.CtcCalculator;
import com.employee.management.util.Formatters;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.type.PdfVersionEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PDFService {

    private final Mapper mapper;
    private final CtcCalculator calculator;
    private final Formatters formatters;
    private final PayRollService payRollService;
    private final AmountToWordsConverter amountToWordsConverter;
    private final PDFGeneratorForPaySlip pdfGeneratorForPaySlip;

    @Autowired
    public PDFService(Mapper mapper, CtcCalculator calculator, Formatters formatters, PayRollService payRollService, AmountToWordsConverter amountToWordsConverter, PDFGeneratorForPaySlip pdfGeneratorForPaySlip) {
        this.mapper = mapper;
        this.calculator = calculator;
        this.formatters = formatters;
        this.payRollService = payRollService;
        this.amountToWordsConverter = amountToWordsConverter;
        this.pdfGeneratorForPaySlip = pdfGeneratorForPaySlip;
    }


    public byte[] generatePaySlipPdf(PaySlip paySlip, String amountInWords) throws JRException, IOException {
        JasperReport jasperReport = compileReport("/templates/pay-slip.jrxml");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("payroll", paySlip.getPayrollDTO());
        parameters.put("employee", paySlip.getEmployeeDTO());
        parameters.put("amount", amountInWords);
        JasperPrint jasperPrint = fillReport(jasperReport, parameters);
        return exportReportToPdf(jasperPrint);
    }

    public byte[] generateHikeLetter(EmployeeDTO employee, HikeEntity hike,String issueHikeDate) throws JRException, IOException {
        JasperReport template1 = compileReport(hike.getIsPromoted() ? "/templates/hikeLetterPages/hike-letter-with-promotion.jrxml" : "/templates/hikeLetterPages/hike-letter.jrxml");
        JasperReport template2 = compileReport("/templates/hikeLetterPages/hike-letter-page-two.jrxml");
        Map<String, Object> parameters1 = new HashMap<>();
        parameters1.put("employee", employee);
        parameters1.put("hikeDetails", mapper.convertToHikeEntityDto(hike));
        parameters1.put("hikeAmount", formatters.formatAmountWithCommas(hike.getNewSalary() - hike.getPrevSalary()));
        parameters1.put("currentDate", issueHikeDate);
        Map<String, Object> parameters2 = new HashMap<>();
        parameters2.put("employee", employee);
        parameters2.put("prevSalaryDetails", calculator.compensationDetails(hike.getPrevSalary()));
        parameters2.put("newSalaryDetails", calculator.compensationDetails(hike.getNewSalary()));
        JasperPrint jasperPrint1 = fillReport(template1, parameters1);
        JasperPrint jasperPrint2 = fillReport(template2, parameters2);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(SimpleExporterInput.getInstance(List.of(jasperPrint1, jasperPrint2)));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.setConfiguration(createPdfExporterConfiguration());
        exporter.exportReport();
        return outputStream.toByteArray();
    }

    public byte[] generateMergedOfferReport(OfferLetterDTO offerLetterDTO) throws IOException, JRException {
        CtcData data = calculator.compensationDetails(formatters.convertStringToDoubleAmount(offerLetterDTO.getCtc()));
        JasperReport report1 = compileReport("/templates/offerLetterPages/pageone.jrxml");
        JasperReport report2 = compileReport("/templates/offerLetterPages/pagetwo.jrxml");
        JasperReport report3 = compileReport("/templates/offerLetterPages/pagethree.jrxml");
        JasperReport report4 = compileReport("/templates/offerLetterPages/pagefour.jrxml");
        Map<String, Object> paramsForReport = new HashMap<>();
        paramsForReport.put("offer", offerLetterDTO);
        paramsForReport.put("ctc", data);
        JasperPrint jasperPrint1 = fillReport(report1, paramsForReport);
        JasperPrint jasperPrint2 = fillReport(report2, null);
        JasperPrint jasperPrint3 = fillReport(report3, null);
        JasperPrint jasperPrint4 = fillReport(report4, paramsForReport);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(SimpleExporterInput.getInstance(List.of(jasperPrint1, jasperPrint2, jasperPrint3, jasperPrint4)));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();
        return outputStream.toByteArray();
    }

    public ResponseEntity<byte[]> generatePdfPreviewResponse(byte[] pdfBytes) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("inline").build());
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private JasperReport compileReport(String path) throws JRException, IOException {
        InputStream template = getClass().getResourceAsStream(path);
        return JasperCompileManager.compileReport(template);
    }

    private JasperPrint fillReport(JasperReport report, Map<String, Object> parameters) throws JRException {
        return JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
    }

    private byte[] exportReportToPdf(JasperPrint jasperPrint) throws JRException {
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private SimplePdfExporterConfiguration createPdfExporterConfiguration() {
        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
        configuration.setPdfVersion(PdfVersionEnum.VERSION_1_7);
        configuration.setCreatingBatchModeBookmarks(true);
        configuration.setOverrideHints(true);
        return configuration;
    }
    public ResponseEntity<byte[]> generatePaySlipResponseEntity(String empId, String payPeriod, boolean isDownload) {
        PaySlip paySlip = payRollService.getPaySlip(empId, payPeriod);
        String amountInWords = amountToWordsConverter.convertToIndianCurrency(paySlip.getPayrollDTO().getTotalNetPayable());
        try {
            byte[] pdfBytes = pdfGeneratorForPaySlip.generatePaySlipPdf(paySlip, amountInWords);
            HttpHeaders headers = createPdfResponseHeaders(paySlip, isDownload);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (JRException e) {
            e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private HttpHeaders createPdfResponseHeaders(PaySlip paySlip, boolean isDownload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "pay_slip(" + paySlip.getEmployeeDTO().getEmployeeName() + "_" + paySlip.getPayrollDTO().getPayPeriod() + ").pdf";
        headers.setContentDisposition(isDownload ? ContentDisposition.builder("attachment").filename(filename).build() : ContentDisposition.builder("inline").filename(filename).build());
        return headers;
    }
}
