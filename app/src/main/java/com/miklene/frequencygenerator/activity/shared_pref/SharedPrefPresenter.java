package com.miklene.frequencygenerator.activity.shared_pref;

import android.content.SharedPreferences;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.miklene.frequencygenerator.repository.WaveRepository;
import com.miklene.frequencygenerator.wave.SimpleWaveFactory;
import com.miklene.frequencygenerator.wave.Wave;
import com.miklene.frequencygenerator.wave.WaveType;

@InjectViewState
public class SharedPrefPresenter extends MvpPresenter<SharedPrefView> {

    WaveRepository sharedPrefRepository;

    public SharedPrefPresenter(WaveRepository sharedPrefRepository) {
        this.sharedPrefRepository = sharedPrefRepository;
    }

    public void load(){
        Wave wave = sharedPrefRepository.load();
        getViewState().setEditTextValue(wave.getFrequency());
    }

    public void save(String type, float frequency, int volume){
        sharedPrefRepository.save(new SimpleWaveFactory()
                .createWave(WaveType.valueOf(type.toUpperCase()),frequency,volume));
    }
}
