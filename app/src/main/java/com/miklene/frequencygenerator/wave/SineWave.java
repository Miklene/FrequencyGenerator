package com.miklene.frequencygenerator.wave;

import org.jetbrains.annotations.NotNull;

public class SineWave extends Wave {

    private double currentVolume;
    private double myPreviousVolume;

    public SineWave(float frequency, double volume, double left, double right) {
        super(frequency, volume, left, right);
    }

    @Override
    public float[] createBuffer() {
        double volume = this.volume;
        double lastVolume = this.lastVolume;
        double left = this.left;
        double right = this.right;
        double lastLeft = this.lastLeft;
        double lastRight = this.lastRight;
        float[] buffer = new float[duration];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (float) (Math.sin(twoPI *
                    (frequency * i * hertzAtPoint + phase)) * countVolume(i, lastVolume, volume));
        }
        countPhase();
        int stereoBufferLength = buffer.length * 2;
        float[] stereoBuffer = new float[stereoBufferLength];
        for (int i = 0; i < buffer.length; i++) {
            stereoBuffer[2 * i] = (float) (buffer[i] * countVolume(i, lastLeft, left));
            stereoBuffer[2 * i + 1] = (float) (buffer[i] * countVolume(i, lastRight, right));
        }
       /* float correctionValue = findMaxAmplitude(stereoBuffer)*1.2f;
        for (int i = 0; i < stereoBuffer.length; i++) {
            stereoBuffer[i] = stereoBuffer[i] / correctionValue;
        }*/
        this.lastVolume = volume;
        this.lastLeft = left;
        this.lastRight = right;
        return stereoBuffer;
        //   return buffer;
    }

    private float findMaxAmplitude(float[] buffer) {
        float max;
        max = buffer[0];
        for (int i = 0; i < buffer.length; i++) {
            if (max < Math.abs(buffer[i]))
                max = Math.abs(buffer[i]);
        }
        return max;
    }

    private double countVolume(int i, double last, double current) {
        if (last == current)
            return current;
        if (i <= 500)
            return last + (current - last) * i / 500;
        return current;
    }

    @NotNull
    @Override
    public String toString() {
        return "Sine";
    }
}
