package com.miklene.frequencygenerator.repository;

import com.miklene.frequencygenerator.wave.Wave;

import io.reactivex.rxjava3.core.Single;

public interface WaveRepository {

    void saveVolume(int volume);
    Single<Integer> loadVolume();
    void saveFrequency(float frequency);
    float loadFrequency();
    void saveWaveType(String waveType);
    String loadWaveType();
    void saveAll(Wave wave);
    Wave loadAll();
}
