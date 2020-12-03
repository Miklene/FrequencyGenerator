package com.miklene.frequencygenerator.player;

public class NullPlayback {

    public NullPlayback() {

    }

    public float[] createBuffer(float[] buffer) {

        for (int i = 0; i < buffer.length; i++)
            buffer[i] *= 0;
        return buffer;
    }

}
