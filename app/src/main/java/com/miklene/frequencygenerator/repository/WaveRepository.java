package com.miklene.frequencygenerator.repository;

import com.miklene.frequencygenerator.wave.Wave;

public interface WaveRepository {

    void saveVolume(int volume);
    int loadVolume();
    void saveFrequency(float frequency);
    float loadFrequency();
    void saveWaveType(String waveType);
    String loadWaveType();
    void saveAll(Wave wave);
    Wave loadAll();
}
