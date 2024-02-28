package com.employee.management.converters;

import org.springframework.stereotype.Component;

@Component
public class IndianNumberSystem {

    public String formatNumber(Double number) {
        String numb = String.valueOf(number);
        String numberStr;
        String split = null;
        if (numb.contains(".")) {
            String[] num = numb.split("\\.");
            numberStr = num[0];
            split = num[1];
        } else numberStr = numb;
        StringBuilder result = getStringBuilder(numberStr);
        if(split!=null)
            result.append(".").append(split).append("0");
        return result.toString();
    }

    private StringBuilder getStringBuilder(String numberStr) {
        StringBuilder result = new StringBuilder();

        int len = numberStr.length();
        int count = 0;
        for (int i = len - 1; i >= 0; i--) {
            result.insert(0, numberStr.charAt(i));
            count++;
            if (count == 3 && i != 0) {
                result.insert(0, ",");
            }
            if (count == 5 && i != 0) {
                result.insert(0, ",");
            }
            if (count == 7 && i != 0) {
                result.insert(0, ",");
            }
        }
        return result;
    }
}
