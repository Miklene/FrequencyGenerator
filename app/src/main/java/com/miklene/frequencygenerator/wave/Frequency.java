package com.miklene.frequencygenerator.wave;

public abstract class Frequency {

    float frequency;

    public Frequency(float frequency){
        this.frequency = frequency;
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }
}
