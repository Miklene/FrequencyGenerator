package com.miklene.frequencygenerator.util;

public class FrequencyCounterFactory {

    public static FrequencyCounter getFrequencyCounter(String type, int from, int to)  {
        if (type.equals("logarithmic"))
            return new LogarithmicFrequencyCounter(from, to);
        else
            return new LinearFrequencyCounter(from, to);
    }
}
