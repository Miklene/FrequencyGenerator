package com.miklene.frequencygenerator.util;

public class LogarithmicFrequencyCounter extends FrequencyCounter {


    public LogarithmicFrequencyCounter(int valueFrom, int valueTo) {
        super(valueFrom, valueTo);
    }

    @Override
    public double countFrequency(int seekBarProgress) {
        if (seekBarProgress + progressFrom == progressTo)
            return valueTo;
        float value = (float) (Math.pow(2, ((seekBarProgress + progressFrom) / 1000000d)));
        double scale = Math.pow(10, 2);
        double result = Math.round(value * scale) / scale;
        return (float) result;
    }

    @Override
    public int countProgress(double frequency) {
        int progress = (int) ((Math.log(frequency) / Math.log(2)) * 1000000);
        if(progress<=progressFrom)
            return 0;
        if(progress>= progressTo)
            return progressTo-progressFrom;
        progress -= progressFrom;
        return progress;
    }

    @Override
    protected int getProgress(double frequency) {
        return (int) ((Math.log(frequency) / Math.log(2)) * 1000000);
    }
}
