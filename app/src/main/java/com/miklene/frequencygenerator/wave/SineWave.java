package com.miklene.frequencygenerator.wave;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SineWave extends Wave {

    public SineWave(float frequency, double amplitude, int harmonicsNumber) {
        super(frequency, amplitude, harmonicsNumber);
    }

    @Override
    void createMainTone(float frequency, double amplitude) {
        mainTone = new Harmonic(frequency, amplitude);
    }

    @Override
    void createWaveHarmonics(float frequency, double amplitude, int harmonicsNumber) {
        for (int i = 0; i < harmonicsNumber; i++)
            waveHarmonics.add(new Harmonic(frequency * (i + 2), amplitude));
    }

    @Override
    public float[] getBuffer() {
        float[] buffer = mainTone.getBuffer();
        float[] harmonicsBuffer;
        for (Harmonic harmonic : waveHarmonics) {
            harmonicsBuffer = harmonic.getBuffer();
            addBuffers(buffer, harmonicsBuffer);
        }
        int stereoBufferLength = buffer.length * 2;
        float[] stereoBuffer = new float[stereoBufferLength];
        float correctionValue = findMaxAmplitude(buffer) * 1.2f;
        for (int i = 0; i < buffer.length; i++) {
            stereoBuffer[2 * i] = buffer[i] / correctionValue;
            stereoBuffer[2 * i + 1] = buffer[i] / correctionValue;
        }
        return stereoBuffer;
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

    private void addBuffers(float[] buffer, float[] harmonicsBuffer) {
        for (int i = 0; i < buffer.length; i++)
            buffer[i] += harmonicsBuffer[i];
    }
}
