package com.miklene.frequencygenerator.ui.activities;

import java.util.Locale;

public class FrequencyParser {

    public static int parseToInt(double value){
        return (int) ((Math.log(value) / Math.log(2)) * 1000000);
    }

    public static float parseToFloat(int value){
        double scale = Math.pow(10, 2);
        double res = Math.round(value * scale) / scale;
        String result =  String.format(Locale.getDefault(),"%.2f", res);
        return Float.parseFloat(result);
    }
}
