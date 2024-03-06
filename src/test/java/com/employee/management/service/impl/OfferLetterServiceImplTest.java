package com.employee.management.service.impl;

import com.employee.management.DTO.CtcData;
import com.employee.management.DTO.OfferLetterDTO;
import com.employee.management.converters.Mapper;
import com.employee.management.models.OfferLetterEntity;
import com.employee.management.repository.OfferLetterRepository;
import com.employee.management.util.CtcCalculator;
import com.employee.management.util.Formatters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OfferLetterServiceImplTest {

    @Mock
    OfferLetterRepository offerLetterRepository;

    @Mock
    Mapper mapper;

    @Mock
    CtcCalculator calculator;

    @Mock
    Formatters formatters;

    @InjectMocks
    OfferLetterServiceImpl offerLetterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testIssueNewOfferLetter() {
        OfferLetterDTO offerLetterDTO = new OfferLetterDTO();
        OfferLetterEntity offerLetterEntity = new OfferLetterEntity();
        OfferLetterEntity savedOfferLetterEntity = new OfferLetterEntity();
        OfferLetterDTO savedOfferLetterDTO = new OfferLetterDTO();

        when(mapper.convertToOfferLetterEntity(offerLetterDTO)).thenReturn(offerLetterEntity);
        when(offerLetterRepository.save(offerLetterEntity)).thenReturn(savedOfferLetterEntity);
        when(mapper.convertToOfferLetterDto(savedOfferLetterEntity)).thenReturn(savedOfferLetterDTO);

        OfferLetterDTO result = offerLetterService.issueNewOfferLetter(offerLetterDTO);

        assertEquals(savedOfferLetterDTO, result);
        verify(mapper, times(1)).convertToOfferLetterEntity(offerLetterDTO);
        verify(offerLetterRepository, times(1)).save(offerLetterEntity);
        verify(mapper, times(1)).convertToOfferLetterDto(savedOfferLetterEntity);
    }

    @Test
    void testPreview() {
        String grossSalary = "10000";
        double grossSalaryDouble = 10000.0;
        CtcData ctcData = new CtcData();

        when(formatters.convertStringToDoubleAmount(grossSalary)).thenReturn(grossSalaryDouble);
        when(calculator.compensationDetails(grossSalaryDouble)).thenReturn(ctcData);

        CtcData result = offerLetterService.preview(grossSalary);

        assertEquals(ctcData, result);
        verify(formatters, times(1)).convertStringToDoubleAmount(grossSalary);
        verify(calculator, times(1)).compensationDetails(grossSalaryDouble);
    }

    @Test
    void testGet() {
        Long id = 1L;
        OfferLetterEntity offerLetterEntity = new OfferLetterEntity();
        OfferLetterDTO offerLetterDTO = new OfferLetterDTO();

        when(offerLetterRepository.findById(id)).thenReturn(Optional.of(offerLetterEntity));
        when(mapper.convertToOfferLetterDto(offerLetterEntity)).thenReturn(offerLetterDTO);

        OfferLetterDTO result = offerLetterService.get(id);

        assertEquals(offerLetterDTO, result);
        verify(offerLetterRepository, times(1)).findById(id);
        verify(mapper, times(1)).convertToOfferLetterDto(offerLetterEntity);
    }
}
