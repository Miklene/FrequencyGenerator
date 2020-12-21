package com.miklene.frequencygenerator.activity;

import com.arellomobile.mvp.MvpView;

public interface PlaybackView extends MvpView {

    void setImageButtonPlayBackground(int drawableId);
    void setEditTextValue(String value);
    void setSeekBarProgress(int progress);
    void vibrate();
    void subscribeEditTextObservable();
    void disposeEditTextObservable();
    //int getUserSeekBarValue();
    //void setUserSeekBarValue(int userSeekBarValue);



}
