package com.miklene.frequencygenerator.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.miklene.frequencygenerator.mvp.views.WaveTypeView;
import com.miklene.frequencygenerator.repository.WaveRepository;
import com.miklene.frequencygenerator.wave.WaveType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@InjectViewState
public class WaveTypePresenter extends MvpPresenter<WaveTypeView> {

    private final WaveRepository sharedPrefRepository;
    private final Map<String,Integer> waveTypes = new HashMap<>();
    public WaveTypePresenter(WaveRepository sharedPrefRepository) {
        this.sharedPrefRepository = sharedPrefRepository;
        waveTypes.put(WaveType.SINE.toString(),0);
        waveTypes.put(WaveType.SAWTOOTH.toString(),1);
        waveTypes.put(WaveType.TRIANGLE.toString(),2);
        waveTypes.put(WaveType.SQUARE.toString(),3);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().initWaveTypeViews();
        setWaveType();
    }

    private void setWaveType(){
        String waveType = sharedPrefRepository.loadWaveType();
        getViewState().setSpinnerWaveTypeItem(waveTypes.get(waveType));
    }

    public void onSpinnerItemSelected(int item){
        Completable.fromAction(() -> sharedPrefRepository.saveWaveType(findKeyByValue(item)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private String findKeyByValue(int key){
        Set<Map.Entry<String,Integer>> entrySet=waveTypes.entrySet();
        Integer value = key;//что хотим найти
        for (Map.Entry<String,Integer> pair : entrySet) {
            if (value.equals(pair.getValue())) {
                return pair.getKey();// нашли наше значение и возвращаем  ключ
            }
        }
        return WaveType.SINE.toString();
    }
}
