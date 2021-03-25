package com.miklene.frequencygenerator.util;

public class LinearFrequencyCounter extends FrequencyCounter{


    public LinearFrequencyCounter(int valueFrom, int valueTo) {
        super(valueFrom, valueTo);
    }

    @Override
    public double countFrequency(int seekBarProgress) {
        return seekBarProgress + progressFrom;
    }

    @Override
    public int countProgress(double frequency) {
        return (int)frequency - progressFrom;
    }

    @Override
    protected int getProgress(double frequency) {
        return (int)frequency;
    }
}
