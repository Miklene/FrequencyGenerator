package com.miklene.frequencygenerator.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.miklene.frequencygenerator.mvp.views.FrequencyView;
import com.miklene.frequencygenerator.repository.WaveRepository;

import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@InjectViewState
public class FrequencyPresenter extends MvpPresenter<FrequencyView> {

    private WaveRepository sharedPrefRepository;
    private float frequency;

    public FrequencyPresenter(WaveRepository sharedPrefRepository) {
        this.sharedPrefRepository = sharedPrefRepository;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().initFrequencyViews();
    }

    public void initFrequency(){

    }

   /* public void load(){
        getViewState().setFrequency(sharedPrefRepository.loadFrequency());
    }*/

   public void onEditTextFrequencyChanged(){

   }

   public void onSeekBarFrequencyChanged(int progress){
       if (progress == 14425215)
           frequency = 22000.00f;
       else
           frequency = calculateFrequency(progress);
       Completable.fromAction(() -> sharedPrefRepository.saveFrequency(frequency))
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .doOnComplete(()->getViewState().setEditTextFrequencyText(formatStringValue(frequency)))
               .subscribe();
   }

    private float calculateFrequency(int seekBarProgress) {
        float value = (float) (Math.pow(2, (seekBarProgress / 1000000d)));
        double scale = Math.pow(10, 2);
        double result = Math.round(value * scale) / scale;
        return (float) result;
    }

    private String formatStringValue(float value) {
        return String.format(Locale.getDefault(),"%.2f", value);
    }
}
