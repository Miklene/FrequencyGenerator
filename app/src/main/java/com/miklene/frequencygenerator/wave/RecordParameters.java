package com.miklene.frequencygenerator.wave;

public interface RecordParameters {
    int sampleRate = 44100;
    double hertzAtPoint = 1d / sampleRate;
    float twoPI = (float) (2 *Math.PI);
    int duration = 1000;
    double durationPerSampleRate = (double)duration / sampleRate;

}
