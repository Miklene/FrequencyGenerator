package com.miklene.frequencygenerator.wave;

public class SimpleWaveFactory {

    public static Wave createWave(WaveType waveType, float frequency, double volume, double left, double right) throws ClassNotFoundException {
        //Wave wave = new SineWave(frequency, volume, left, right);
        if (waveType.equals(WaveType.SINE))
            return new SineWave(frequency, volume, left, right);
        if (waveType.equals(WaveType.SAWTOOTH))
            return new SawWave(frequency, volume, left, right);
        if (waveType.equals(WaveType.SQUARE))
            return new SquareWave(frequency, volume, left, right);
        if (waveType.equals(WaveType.TRIANGLE))
            return new TriangleWave(frequency, volume, left, right);
        throw new ClassNotFoundException();
    }
}
