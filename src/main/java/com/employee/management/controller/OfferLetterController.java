package com.employee.management.controller;

import com.employee.management.DTO.CtcData;
import com.employee.management.DTO.OfferLetterDTO;
import com.employee.management.service.PDFService;
import com.employee.management.service.EmailSenderService;
import com.employee.management.service.OfferLetterService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/offer-letter")
public class OfferLetterController {

    private final OfferLetterService offerLetterService;
    private final EmailSenderService emailSenderService;
    private final PDFService pdfService;

    @Autowired
    public OfferLetterController(OfferLetterService offerLetterService, EmailSenderService emailSenderService, PDFService pdfService) {
        this.offerLetterService = offerLetterService;
        this.emailSenderService = emailSenderService;
        this.pdfService = pdfService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> issueOfferLetter(@RequestBody OfferLetterDTO offerLetterDTO) {
        try {
            OfferLetterDTO letterDTO = offerLetterService.issueNewOfferLetter(offerLetterDTO);
            byte[] pdfBytes = pdfService.generateMergedOfferReport(letterDTO);
            emailSenderService.sendEmailWithAttachment(letterDTO.getEmail(), "Offer and Appointment Letter", "Congratulations", pdfBytes);
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/preview-compensation-details")
    public ResponseEntity<CtcData> preview(@RequestBody OfferLetterDTO offerLetterDTO) {
        return ResponseEntity.ok(offerLetterService.preview(offerLetterDTO.getCtc()));
    }

    @PostMapping("/preview-letter")
    public ResponseEntity<byte[]> previewLetter(@RequestBody OfferLetterDTO offerLetterDTO) throws IOException, JRException {
        byte[] pdfBytes = pdfService.generateMergedOfferReport(offerLetterDTO);
        return pdfService.generatePdfPreviewResponse(pdfBytes);
    }

    @GetMapping("/preview-letter-by-id/{id}")
    public ResponseEntity<byte[]> previewLetterById(@PathVariable("id") Long id) throws IOException, JRException {
        OfferLetterDTO offerLetterDTO = offerLetterService.get(id);
        byte[] pdfBytes = pdfService.generateMergedOfferReport(offerLetterDTO);
        return pdfService.generatePdfPreviewResponse(pdfBytes);
    }
}
