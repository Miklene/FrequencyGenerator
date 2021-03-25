package com.miklene.frequencygenerator.util;

public class LinearFrequencyCounter extends FrequencyCounter{


    public LinearFrequencyCounter(int progressFrom, int progressTo) {
        super(progressFrom, progressTo);
    }

    @Override
    public double countFrequency(int seekBarProgress) {
        return seekBarProgress + progressFrom;
    }

    @Override
    public int countProgress(double frequency) {
        return (int)frequency - progressFrom;
    }
}
