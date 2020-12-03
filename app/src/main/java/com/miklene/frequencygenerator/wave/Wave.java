package com.miklene.frequencygenerator.wave;

import java.util.ArrayList;

public abstract class Wave {
    WaveType type;
    Harmonic mainTone;
    ArrayList<Harmonic> waveHarmonics = new ArrayList<>();
    int imageId;


    public Wave(float frequency, double amplitude, int harmonicsNumber) {
        createMainTone(frequency, amplitude);
        createWaveHarmonics(frequency, amplitude, harmonicsNumber);
    }

    abstract void createMainTone(float frequency, double amplitude);

    abstract void createWaveHarmonics(float frequency, double amplitude, int harmonicsNumber);

    public abstract float[] getBuffer();
}
