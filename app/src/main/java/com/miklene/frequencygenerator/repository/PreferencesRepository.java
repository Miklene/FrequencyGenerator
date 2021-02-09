package com.miklene.frequencygenerator.repository;

import android.content.SharedPreferences;
import android.util.Log;

import com.miklene.frequencygenerator.wave.SimpleWaveFactory;
import com.miklene.frequencygenerator.wave.Wave;
import com.miklene.frequencygenerator.wave.WaveType;


public class PreferencesRepository implements WaveRepository{

    private SharedPreferences preferences;
    private static final String PREFS_FILE = "Wave";
    private static final String PREFS_FREQUENCY = "Frequency";
    private static final String PREFS_VOLUME = "Volume";
    private static final String PREFS_WAVE_TYPE = "WaveType";

    public PreferencesRepository(SharedPreferences preferences) {

        this.preferences = preferences;
    }


    @Override
    public void saveVolume(int volume) {
        getEditor().putInt(PREFS_VOLUME,volume).apply();
    }

    @Override
    public int loadVolume() {
        return preferences.getInt(PREFS_VOLUME,100);
    }

    @Override
    public void saveFrequency(float frequency) {
        getEditor().putFloat(PREFS_FREQUENCY, frequency).apply();
    }

    @Override
    public float loadFrequency() {
        return preferences.getFloat(PREFS_FREQUENCY, 200);
    }

    @Override
    public void saveWaveType(String waveType) {
        getEditor().putString(PREFS_WAVE_TYPE, waveType).apply();
    }

    @Override
    public String loadWaveType() {
        return preferences.getString(PREFS_WAVE_TYPE,WaveType.SINE.toString());
    }

    @Override
    public void saveAll(Wave wave) {
        SharedPreferences.Editor prefEditor;
        prefEditor = preferences.edit();
        prefEditor.putFloat(PREFS_FREQUENCY,wave.getFrequency());
        prefEditor.putInt(PREFS_VOLUME,(int)(wave.getAmplitude()*100));
        prefEditor.putString(PREFS_WAVE_TYPE, wave.toString().toUpperCase());
        prefEditor.apply();
    }

    @Override
    public Wave loadAll() {
        float frequency = preferences.getFloat(PREFS_FREQUENCY,200);
        String type = preferences.getString(PREFS_WAVE_TYPE, WaveType.SINE.toString());
        int amplitude = preferences.getInt(PREFS_VOLUME, 100);
        return new SimpleWaveFactory().createWave(WaveType.valueOf(type),frequency,amplitude);
    }

    private SharedPreferences.Editor getEditor() {
        return preferences.edit();
    }

}
