package com.miklene.frequencygenerator.wave;

import android.util.Log;

import java.util.ArrayList;

public abstract class Wave implements RecordParameters {

    WaveType type;
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

}
