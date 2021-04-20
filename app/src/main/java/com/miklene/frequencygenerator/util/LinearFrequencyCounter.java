package com.miklene.frequencygenerator.util;

public class LinearFrequencyCounter extends FrequencyCounter {


    public LinearFrequencyCounter(int valueFrom, int valueTo) {
        super(valueFrom, valueTo);
    }

    @Override
    public double countFrequency(int seekBarProgress) {
        if (seekBarProgress + progressFrom == progressTo)
            return valueTo;
        return seekBarProgress + valueFrom;
    }

    @Override
    public int countProgress(double frequency) {
        if (frequency <= progressFrom)
            return 0;
        if (frequency >= progressTo)
            return progressTo - progressFrom;
        return (int) frequency - progressFrom;
    }

    @Override
    protected int getProgress(double frequency) {
        return (int) frequency;
    }
}
