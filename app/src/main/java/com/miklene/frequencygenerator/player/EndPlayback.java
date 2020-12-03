package com.miklene.frequencygenerator.player;

public class EndPlayback {

    public EndPlayback() {

    }

    public float[] createBuffer(float[] buffer) {
        int shift = buffer.length - 550;
        for (int i = shift; i < buffer.length; i++)
            buffer[i] *= calculateDecrease(i - shift);
        return buffer;
    }

    private double calculateDecrease(int item) {
        if (item < 500)
            return (500 - item) * 0.002;
        return 0;
    }
}
