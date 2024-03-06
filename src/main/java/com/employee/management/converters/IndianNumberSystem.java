package com.employee.management.converters;


import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

@Component
public class IndianNumberSystem {

    public String formatNumber(Double number) {
        Double round= (double) Math.round(number);
        String numb = String.valueOf(round);
        String numberStr;
        String split = null;
        if (numb.contains(".")) {
            String[] num = numb.split("\\.");
            numberStr = num[0];
            split = num[1];
        } else numberStr = numb;
        StringBuilder result= new StringBuilder(numberStr);
        if(split!=null) {
            result.append(".").append(split);
        }
        result= formatNumb(result);
        StringBuilder resultW = getStringBuilder(String.valueOf(result));
        return resultW.toString();
    }
    private StringBuilder formatNumb(StringBuilder result) {
        String resultStr = result.toString().replaceAll(",", "");
        DecimalFormat df = new DecimalFormat("##0.00");
        String formattedResultStr = df.format(Double.parseDouble(resultStr));

        return new StringBuilder(formattedResultStr);
    }

    private StringBuilder getStringBuilder(String numberStr) {
        StringBuilder result = new StringBuilder();

        int len = numberStr.length();
        int count = 0;
        for (int i = len - 1; i >= 0; i--) {
            result.insert(0, numberStr.charAt(i));
            count++;
            if (count == 6 && i != 0) {
                result.insert(0, ",");
            }
            if (count == 8 && i != 0) {
                result.insert(0, ",");
            }
            if (count == 10 && i != 0) {
                result.insert(0, ",");
            }
        }
        return result;
    }
}
