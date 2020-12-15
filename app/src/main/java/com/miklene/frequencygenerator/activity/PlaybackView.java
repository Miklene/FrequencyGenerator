package com.miklene.frequencygenerator.activity;

import com.arellomobile.mvp.MvpView;

public interface PlaybackView extends MvpView {

    void setImageButtonPlayBackground(int drawableId);
    void setEditTextValue(int value);
    //int getUserSeekBarValue();
    //void setUserSeekBarValue(int userSeekBarValue);



}
