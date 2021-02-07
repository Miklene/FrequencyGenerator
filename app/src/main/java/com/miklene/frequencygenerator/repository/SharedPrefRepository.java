package com.miklene.frequencygenerator.repository;

import android.content.SharedPreferences;
import android.util.Log;

import com.miklene.frequencygenerator.wave.SimpleWaveFactory;
import com.miklene.frequencygenerator.wave.Wave;
import com.miklene.frequencygenerator.wave.WaveType;

public class SharedPrefRepository implements WaveRepository{

    private SharedPreferences preferences;
    private static final String PREFS_FILE = "Wave";
    private static final String PREFS_FREQUENCY = "Frequency";
    private static final String PREFS_AMPLITUDE = "Amplitude";
    private static final String PREFS_WAVE_TYPE = "WaveType";

    public SharedPrefRepository(SharedPreferences preferences) {

        this.preferences = preferences;
    }

    @Override
    public void save(Wave wave) {
        SharedPreferences.Editor prefEditor;
        prefEditor = preferences.edit();
        prefEditor.putFloat(PREFS_FREQUENCY,wave.getFrequency());
        prefEditor.putInt(PREFS_AMPLITUDE,(int)(wave.getAmplitude()*100));
        prefEditor.putString(PREFS_WAVE_TYPE, wave.toString().toUpperCase());
        prefEditor.apply();
    }

    @Override
    public Wave load() {
        float frequency = preferences.getFloat(PREFS_FREQUENCY,200);
        String type = preferences.getString(PREFS_WAVE_TYPE, WaveType.SINE.toString());
        WaveType waveType = WaveType.SINE;
        if(type.equals(WaveType.SAWTOOTH.toString()))
            waveType = WaveType.SAWTOOTH;
        int amplitude = preferences.getInt(PREFS_AMPLITUDE, 100);
        return new SimpleWaveFactory().createWave(waveType,frequency,amplitude);
    }
}
