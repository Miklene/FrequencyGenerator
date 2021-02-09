package com.miklene.frequencygenerator.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.miklene.frequencygenerator.mvp.views.BalanceView;
import com.miklene.frequencygenerator.repository.WaveRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@InjectViewState
public class BalancePresenter extends MvpPresenter<BalanceView> {

    private final WaveRepository sharedPrefRepository;
    private int right;
    private int left;


    public BalancePresenter(WaveRepository sharedPrefRepository) {
        this.sharedPrefRepository = sharedPrefRepository;
    }

    public void initBalance() {
        sharedPrefRepository.loadRightChannel()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(this::setRightChannel)
                .doOnSuccess(i->sharedPrefRepository.loadLeftChannel()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSuccess(this::setLeftChannel)
                        .doOnSuccess(integer->setSeekBarProgress())
                        .subscribe())
                .subscribe();
    }

    public void seekBarBalanceProgressChanged(int progress){
        if(progress>100) {
            right = 100;
            left = 200-progress;
        } else {
            right = progress;
            left = 100;
        }
        Completable.fromAction(()->sharedPrefRepository.saveRightChannel(right))
                .subscribeOn(Schedulers.io())
                .subscribe();
        Completable.fromAction(()->sharedPrefRepository.saveLeftChannel(left))
                .subscribeOn(Schedulers.io())
                .subscribe();
        setRightChannel(right);
        setLeftChannel(left);
    }

    private void setSeekBarProgress(){
        getViewState().setSeekBarBalanceProgress(calculateSeekBarProgress());
    }

    private int calculateSeekBarProgress(){
        return right+100-left;
    }

    private void setRightChannel(int right){
        this.right=right;
        getViewState().setTextViewRightChannelValue(getStringValueOfVolume(right));
    }

    private void setLeftChannel(int left){
        this.left=left;
        getViewState().setTextViewLeftChannelValue(getStringValueOfVolume(left));
    }

    private String getStringValueOfVolume(int volumeValue) {
        return volumeValue + "%";
    }
}
