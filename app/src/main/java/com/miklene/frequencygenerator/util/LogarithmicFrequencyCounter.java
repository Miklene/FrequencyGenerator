package com.miklene.frequencygenerator.util;

public class LogarithmicFrequencyCounter extends FrequencyCounter {


    public LogarithmicFrequencyCounter(int valueFrom, int valueTo) {
        super(valueFrom, valueTo);
    }

    @Override
    public double countFrequency(int seekBarProgress) {
        float value = (float) (Math.pow(2, (seekBarProgress / 1000000d)));
        double scale = Math.pow(10, 2);
        double result = Math.round(value * scale) / scale;
        return (float) result;
    }

    @Override
    public int countProgress(double frequency) {
        return (int) ((Math.log(frequency) / Math.log(2)) * 1000000);
    }

    @Override
    protected int getProgress(double frequency) {
        return (int) ((Math.log(frequency) / Math.log(2)) * 1000000);
    }
}
