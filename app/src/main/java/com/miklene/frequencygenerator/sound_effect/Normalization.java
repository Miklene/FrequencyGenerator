package com.miklene.frequencygenerator.sound_effect;



public class Normalization extends SoundEffectDecorator {



    public float[] createBuffer(float[] buffer) {
        float correctionValue = findMaxAmplitude(buffer)*1.2f;
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = buffer[i] / correctionValue;
        }
        return buffer;
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
}
