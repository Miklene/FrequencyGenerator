package com.miklene.frequencygenerator.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.miklene.frequencygenerator.mvp.views.FrequencyView;
import com.miklene.frequencygenerator.repository.WaveRepository;

@InjectViewState
public class FrequencyPresenter extends MvpPresenter<FrequencyView> {

    private WaveRepository sharedPrefRepository;

    public FrequencyPresenter(WaveRepository sharedPrefRepository) {
        this.sharedPrefRepository = sharedPrefRepository;
    }

    public void load(){
        getViewState().setFrequency(sharedPrefRepository.loadFrequency());
    }

   public void onEditTextFrequencyChanged(){

   }

   public void onSeekBarFrequencyChanged(){

   }
}
