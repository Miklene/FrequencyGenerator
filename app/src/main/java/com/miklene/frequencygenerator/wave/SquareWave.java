package com.miklene.frequencygenerator.wave;

public class SquareWave extends Wave{

    public SquareWave(float frequency, double volume, double left, double right) {
        super(frequency, volume, left, right);
    }

    @Override
    public float[] createBuffer() {
        float[] buffer = new float[duration];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (float) (Math.signum(Math.sin(twoPI *
                    (frequency * i * hertzAtPoint + phase)))*2*volume/Math.PI);
        }
        countPhase();
        int stereoBufferLength = buffer.length*2;
        float[] stereoBuffer = new float[stereoBufferLength];
        for(int i = 0; i < buffer.length; i++){
            stereoBuffer[2*i] = (float)(buffer[i]*left);
            stereoBuffer[2*i+1] = (float)(buffer[i]*right);
        }
        return stereoBuffer;
    }
}
