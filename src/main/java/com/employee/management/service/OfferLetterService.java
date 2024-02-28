package com.employee.management.service;

import com.employee.management.DTO.CtcData;
import com.employee.management.DTO.OfferLetterDTO;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;

public interface OfferLetterService {
    OfferLetterDTO issueNewOfferLetter(OfferLetterDTO offerLetterDTO);

    byte[] getMergedOfferReport(OfferLetterDTO offerLetterDTO) throws IOException, JRException;

    CtcData preview(String grossSalary);

    OfferLetterDTO get(Long id);
}
