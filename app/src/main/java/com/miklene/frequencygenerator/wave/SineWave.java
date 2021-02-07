package com.miklene.frequencygenerator.wave;

public class SineWave extends Wave {

    public SineWave(float frequency, double amplitude) {
        super(frequency, amplitude);
        phase = 0;
    }

    @Override
    public float[] createBuffer() {
        float[] buffer = new float[duration];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (float) (Math.sin(twoPI *
                    (frequency * i * hertzAtPoint + phase)) * amplitude);
        }
        countPhase();
        int stereoBufferLength = buffer.length*2;
        float[] stereoBuffer = new float[stereoBufferLength];
        for(int i = 0; i < buffer.length; i++){
            stereoBuffer[2*i] = buffer[i];
            stereoBuffer[2*i+1] = buffer[i];
        }
        float correctionValue = findMaxAmplitude(stereoBuffer)*1.2f;
        for (int i = 0; i < stereoBuffer.length; i++) {
            stereoBuffer[i] = stereoBuffer[i] / correctionValue;
        }
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

    @Override
    public String toString() {
        return "Sine";
    }
}
