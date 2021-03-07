package com.miklene.frequencygenerator.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.miklene.frequencygenerator.ui.activities.FrequencyParser;
import com.miklene.frequencygenerator.mvp.views.SharedPrefView;
import com.miklene.frequencygenerator.repository.WaveRepository;
import com.miklene.frequencygenerator.wave.SimpleWaveFactory;
import com.miklene.frequencygenerator.wave.Wave;
import com.miklene.frequencygenerator.wave.WaveType;

import java.util.Locale;

@InjectViewState
public class SharedPrefPresenter extends MvpPresenter<SharedPrefView> {

    WaveRepository sharedPrefRepository;

    public SharedPrefPresenter(WaveRepository sharedPrefRepository) {
        this.sharedPrefRepository = sharedPrefRepository;
    }

    public void load(){
        Wave wave = sharedPrefRepository.loadAll();
        getViewState().setEditTextValue(String.
                format(Locale.getDefault(),"%.2f", wave.getFrequency()));
        getViewState().setSeekBarFrequencyProgress(FrequencyParser.parseToInt(wave.getFrequency()));
        getViewState().setTextViewVolumeValue(wave.getVolume() + "%");
    }

    /*public void save(String type, float frequency, int volume, ){
        sharedPrefRepository.saveAll(new SimpleWaveFactory()
                .createWave(WaveType.valueOf(type.toUpperCase()),frequency,volume, ));
    }*/
}
