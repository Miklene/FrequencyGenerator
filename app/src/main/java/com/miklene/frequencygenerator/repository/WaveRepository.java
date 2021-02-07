package com.miklene.frequencygenerator.repository;

import com.miklene.frequencygenerator.wave.Wave;

public interface WaveRepository {

    void save(Wave wave);
    Wave load();
}
