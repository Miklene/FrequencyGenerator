package com.miklene.frequencygenerator.util;

public abstract class FrequencyCounter {

    protected int valueFrom;
    protected int valueTo;
    protected int progressFrom;
    protected int progressTo;

    public FrequencyCounter(int progressFrom, int progressTo) {
        this.progressFrom = progressFrom;
        this.progressTo = progressTo;
    }

    public abstract double countFrequency(int seekBarProgress);

    public abstract int countProgress(double frequency);


}
