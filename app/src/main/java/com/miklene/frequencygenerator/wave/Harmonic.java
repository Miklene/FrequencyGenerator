package com.miklene.frequencygenerator.wave;

class Harmonic implements RecordParameters {

    private float frequency;
    private double amplitude;
    private double phase;
    private double d;

    Harmonic(float frequency, double amplitude) {
        this.frequency = frequency;
        this.amplitude = amplitude;
        phase = 0;
    }

   float[] getBuffer() {
        float[] buffer = new float[duration];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (float) (Math.sin(twoPI *
                    (frequency * i * hertzAtPoint + phase)) * amplitude);
        }
        countPhase();
        return buffer;
    }

    private void countPhase() {
        double period = 1d / frequency;
        phase = phase + durationPerSampleRate / period;
        phase -= (int) phase;
    }

}
