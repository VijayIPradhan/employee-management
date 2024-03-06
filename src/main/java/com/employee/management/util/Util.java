package com.employee.management.util;

import com.employee.management.DTO.PaySlip;
import com.employee.management.converters.AmountToWordsConverter;
import com.employee.management.converters.PDFGeneratorForPaySlip;
import com.employee.management.service.PayRollService;
import com.employee.management.service.impl.PayRollServiceImpl;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class Util {
    public int getNumberOfDaysInMonth(String period) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
            YearMonth yearMonth = YearMonth.parse(period, formatter);
            return yearMonth.lengthOfMonth();
        } catch (DateTimeParseException e) {
            return -1;
        }
    }

}