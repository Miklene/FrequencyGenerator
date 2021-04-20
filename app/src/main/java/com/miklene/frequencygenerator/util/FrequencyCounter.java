package com.miklene.frequencygenerator.util;

public abstract class FrequencyCounter {

    protected int valueFrom;
    protected int valueTo;
    protected int progressFrom;
    protected int progressTo;

    public FrequencyCounter(int valueFrom, int valueTo) {
        this.valueFrom = valueFrom;
        this.valueTo = valueTo;
        this.progressFrom = getProgress(valueFrom);
        this.progressTo = getProgress(valueTo);
    }

    public abstract double countFrequency(int seekBarProgress);

    public abstract int countProgress(double frequency);

    public float validateFrequency(float frequency){
        if (frequency < valueFrom)
            frequency = valueFrom;
        if (frequency > valueTo)
            frequency = valueTo;
        return frequency;
    }

    public int countSeekBarMax(){
        return progressTo - progressFrom;
    }

    public void setValueFrom(int valueFrom) {
        this.valueFrom = valueFrom;
        progressFrom=getProgress(valueFrom);
    }

    public void setValueTo(int valueTo) {
        this.valueTo = valueTo;
        progressTo=getProgress(valueTo);
    }

    protected abstract int getProgress(double frequency);

}
