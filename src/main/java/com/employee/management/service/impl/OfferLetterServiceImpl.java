package com.employee.management.service.impl;

import com.employee.management.DTO.CtcData;
import com.employee.management.DTO.OfferLetterDTO;
import com.employee.management.converters.Mapper;
import com.employee.management.models.OfferLetterEntity;
import com.employee.management.repository.OfferLetterRepository;
import com.employee.management.service.OfferLetterService;
import com.employee.management.util.CtcCalculator;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OfferLetterServiceImpl implements OfferLetterService {
    @Autowired
    OfferLetterRepository offerLetterRepository;
    @Autowired
    Mapper mapper;
    @Autowired
    CtcCalculator calculator;

    @Override
    public OfferLetterDTO issueNewOfferLetter(OfferLetterDTO offerLetterDTO){
        OfferLetterEntity offerLetter=mapper.convertToOfferLetterEntity(offerLetterDTO);
        OfferLetterEntity savedOfferLetter = offerLetterRepository.save(offerLetter);
        return mapper.convertToOfferLetterDto(savedOfferLetter);
    }
    @Override
    public byte[] getMergedOfferReport(OfferLetterDTO offerLetterDTO) throws IOException, JRException {
        CtcData data=calculator.compensationDetails(mapper.convertStringToDoubleAmount(offerLetterDTO.getCtc()));

        InputStream template = getClass().getResourceAsStream("/templates/pay-slip.jrxml");
        JasperReport report1 = JasperCompileManager.compileReport(new ClassPathResource("/templates/offerLetterPages/pageone.jrxml").getInputStream());
        JasperReport report2 = JasperCompileManager.compileReport(new ClassPathResource("/templates/offerLetterPages/pagetwo.jrxml").getInputStream());
        JasperReport report3 = JasperCompileManager.compileReport(new ClassPathResource("/templates/offerLetterPages/pagethree.jrxml").getInputStream());
        JasperReport report4 = JasperCompileManager.compileReport(new ClassPathResource("/templates/offerLetterPages/pagefour.jrxml").getInputStream());
        Map<String, Object> paramsForReport= new HashMap<>();
        paramsForReport.put("offer",offerLetterDTO);
        paramsForReport.put("ctc",data);


        JasperPrint jasperPrint1 = JasperFillManager.fillReport(report1, paramsForReport, new JREmptyDataSource());
        JasperPrint jasperPrint2 = JasperFillManager.fillReport(report2, null, new JREmptyDataSource());
        JasperPrint jasperPrint3 = JasperFillManager.fillReport(report3, null, new JREmptyDataSource());
        JasperPrint jasperPrint4 = JasperFillManager.fillReport(report4, paramsForReport, new JREmptyDataSource());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        List<JasperPrint> jasperPrints = new ArrayList<>();
        jasperPrints.add(jasperPrint1);
        jasperPrints.add(jasperPrint2);
        jasperPrints.add(jasperPrint3);
        jasperPrints.add(jasperPrint4);
        exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrints));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();

        return outputStream.toByteArray();
    }

    @Override
    public CtcData preview(String grossSalary){
        return calculator.compensationDetails(Double.valueOf(grossSalary));
    }

    @Override
    public OfferLetterDTO get(Long id) {
        return mapper.convertToOfferLetterDto(offerLetterRepository.findById(id).get());
    }
}
