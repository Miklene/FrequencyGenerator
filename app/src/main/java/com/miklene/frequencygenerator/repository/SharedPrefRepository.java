package com.miklene.frequencygenerator.repository;

import android.content.SharedPreferences;

import com.miklene.frequencygenerator.wave.Wave;

public class SharedPrefRepository implements WaveRepository{

    SharedPreferences preferences;
    public SharedPrefRepository(SharedPreferences preferences) {

        this.preferences = preferences;
    }

    @Override
    public void save(Wave wave) {
        SharedPreferences.Editor prefEditor;

    }

    @Override
    public Wave load() {
        return null;
    }
}
