package com.miklene.frequencygenerator.wave;

public class SimpleWaveFactory {

    public Wave createWave(WaveType waveType, float frequency, double amplitude){
        Wave wave = new SineWave(frequency,amplitude);
        if(waveType.equals(WaveType.SINE))
            wave = new SineWave(frequency,amplitude);
        /*if(waveType.equals(WaveType.SAWTOOTH))
            wave= new SawtoothWave(frequency, amplitude);*/
        return wave;
    }

}
