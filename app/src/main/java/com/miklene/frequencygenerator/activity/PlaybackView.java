package com.miklene.frequencygenerator.activity;

import com.arellomobile.mvp.MvpView;

public interface PlaybackView extends MvpView {

    void setImageButtonPlayBackground(int drawableId);
    void setEditTextValue(String frequencyValue);
    void setSeekBarProgress(int progress);
    void vibrate();
    void subscribeEditTextObservable();
    void disposeEditTextObservable();
    void setTextViewVolumeValue(String volumeValue);
    void setImageButtonVolumeSrc(int drawableId);
    void setSeekBarVolumeProgress(int progress);
    //int getUserSeekBarValue();
    //void setUserSeekBarValue(int userSeekBarValue);



}
