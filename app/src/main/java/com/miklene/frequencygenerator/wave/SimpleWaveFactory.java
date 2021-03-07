package com.miklene.frequencygenerator.wave;

public class SimpleWaveFactory {

    public static Wave createWave(WaveType waveType, float frequency, double volume, double left, double right){
        Wave wave = new SineWave(frequency,volume,left,right);
        if(waveType.equals(WaveType.SINE))
            wave = new SineWave(frequency,volume,left,right);
        /*if(waveType.equals(WaveType.SAWTOOTH))
            wave= new SawtoothWave(frequency, amplitude);*/
        return wave;
    }

}
