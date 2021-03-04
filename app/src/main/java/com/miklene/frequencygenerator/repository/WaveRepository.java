package com.miklene.frequencygenerator.repository;

import com.miklene.frequencygenerator.wave.Wave;

import io.reactivex.rxjava3.core.Single;

public interface WaveRepository {

    void saveVolume(int volume);
    Single<Integer> loadVolume();
    void saveFrequency(float frequency);
    Single<Float> loadFrequency();
    void saveWaveType(String waveType);
    Single<String> loadWaveType();
    void saveRightChannel(int right);
    Single<Integer> loadRightChannel();
    void saveLeftChannel(int left);
    Single<Integer> loadLeftChannel();
    void saveAll(Wave wave);
    Wave loadAll();
}
