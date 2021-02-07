package com.miklene.frequencygenerator.wave;

import android.util.Log;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;


public abstract class Wave implements RecordParameters {

    long id = 0;
    float frequency;
    double amplitude;
    double phase;

    public Wave(float frequency, double amplitude) {
        this.frequency = frequency;
        this.amplitude = amplitude;
    }

    public abstract float[] createBuffer();

    void countPhase() {
        double period = 1d / frequency;
        phase = phase + durationPerSampleRate / period;
        phase -= (int) phase;
        Log.d("TAG", String.valueOf(durationPerSampleRate));
    }

    public float getFrequency() {
        return frequency;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public double getPhase() {
        return phase;
    }

    public void setPhase(double phase) {
        this.phase = phase;
    }

    @Override
    public String toString() {
        return "Wave";
    }
}
