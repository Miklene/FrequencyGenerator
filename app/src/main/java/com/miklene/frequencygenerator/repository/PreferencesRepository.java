package com.miklene.frequencygenerator.repository;

import android.content.SharedPreferences;

import com.miklene.frequencygenerator.player.WavePlayer;
import com.miklene.frequencygenerator.wave.SimpleWaveFactory;
import com.miklene.frequencygenerator.wave.Wave;
import com.miklene.frequencygenerator.wave.WaveType;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;


public class PreferencesRepository implements WaveRepository {

    private SharedPreferences preferences;
    private static PreferencesRepository repository;

    private static final String PREFS_FREQUENCY = "Frequency";
    private static final String PREFS_VOLUME = "Volume";
    private static final String PREFS_WAVE_TYPE = "WaveType";
    private static final String PREFS_RIGHT_CHANNEL = "RightChannel";
    private static final String PREFS_LEFT_CHANNEL = "LeftChannel";

    private static Subject<Float> frequencySubject;
    private static Subject<Integer> volumeSubject;
    private static Subject<Integer> rightChannelSubject;
    private static Subject<Integer> leftChannelSubject;
    private static Subject<String> waveTypeSubject;

    public PreferencesRepository(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public Subject<Float> getFrequencySubject() {
        frequencySubject = BehaviorSubject
                .createDefault(preferences.getFloat(PREFS_FREQUENCY, 200));
        return frequencySubject;
    }

    @Override
    public Subject<Integer> getVolumeSubject() {
        volumeSubject = BehaviorSubject
                .createDefault(preferences.getInt(PREFS_VOLUME, 100));
        return volumeSubject;
    }

    @Override
    public Subject<Integer> getRightChannelSubject() {
        rightChannelSubject = BehaviorSubject
                .createDefault(preferences.getInt(PREFS_RIGHT_CHANNEL, 100));
        return rightChannelSubject;
    }

    @Override
    public Subject<Integer> getLeftChannelSubject() {
        leftChannelSubject = BehaviorSubject
                .createDefault(preferences.getInt(PREFS_LEFT_CHANNEL, 100));
        return leftChannelSubject;
    }

    @Override
    public Subject<String> getWaveTypeSubject() {
        waveTypeSubject = BehaviorSubject
                .createDefault(preferences.getString(PREFS_WAVE_TYPE, WaveType.SINE.toString() ));
        return waveTypeSubject;
    }

    @Override
    public void saveVolume(int volume) {
        getEditor().putInt(PREFS_VOLUME, volume).apply();
        if (volumeSubject != null)
            volumeSubject.onNext(volume);
    }

    @Override
    public Single<Integer> loadVolume() {
        return Single.create(subscriber -> subscriber
                .onSuccess(preferences.getInt(PREFS_VOLUME, 100)));
    }

    @Override
    public void saveFrequency(float frequency) {
        getEditor().putFloat(PREFS_FREQUENCY, frequency).apply();
        if (frequencySubject != null)
            frequencySubject.onNext(frequency);
    }

    @Override
    public Single<Float> loadFrequency() {
        return Single.create(subscriber -> subscriber
                .onSuccess(preferences.getFloat(PREFS_FREQUENCY, 200)));
    }

    @Override
    public void saveWaveType(String waveType) {
        getEditor().putString(PREFS_WAVE_TYPE, waveType).apply();
        if (waveTypeSubject != null)
            waveTypeSubject.onNext(waveType);
    }

    @Override
    public Single<String> loadWaveType() {
        return Single.create(subscriber -> subscriber
                .onSuccess(preferences.getString(PREFS_WAVE_TYPE, WaveType.SINE.toString())));
    }

    @Override
    public void saveRightChannel(int right) {
        getEditor().putInt(PREFS_RIGHT_CHANNEL, right).apply();
        if (rightChannelSubject != null)
            rightChannelSubject.onNext(right);
    }

    @Override
    public Single<Integer> loadRightChannel() {
        return Single.create(subscriber ->
                subscriber.onSuccess(preferences.getInt(PREFS_RIGHT_CHANNEL, 100)));
    }

    @Override
    public void saveLeftChannel(int left) {
        getEditor().putInt(PREFS_LEFT_CHANNEL, left).apply();
        if (leftChannelSubject != null)
            leftChannelSubject.onNext(left);
    }

    @Override
    public Single<Integer> loadLeftChannel() {
        return Single.create(subscriber ->
                subscriber.onSuccess(preferences.getInt(PREFS_LEFT_CHANNEL, 100)));
    }

    @Override
    public void saveAll(Wave wave) {
        SharedPreferences.Editor prefEditor;
        prefEditor = preferences.edit();
        prefEditor.putFloat(PREFS_FREQUENCY, wave.getFrequency());
        prefEditor.putInt(PREFS_VOLUME, (int) (wave.getVolume() * 100));
        prefEditor.putString(PREFS_WAVE_TYPE, wave.toString().toUpperCase());
        prefEditor.apply();
    }

    @Override
    public Wave loadAll() {
      /*  float frequency = preferences.getFloat(PREFS_FREQUENCY, 200);
        String type = preferences.getString(PREFS_WAVE_TYPE, WaveType.SINE.toString());
        int amplitude = preferences.getInt(PREFS_VOLUME, 100);
        return new SimpleWaveFactory().createWave(WaveType.valueOf(type), frequency, amplitude);*/
        return null;
    }

    private SharedPreferences.Editor getEditor() {
        return preferences.edit();
    }

}
