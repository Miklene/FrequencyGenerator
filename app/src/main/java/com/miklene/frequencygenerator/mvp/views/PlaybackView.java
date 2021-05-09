package com.miklene.frequencygenerator.mvp.views;

import com.arellomobile.mvp.MvpView;

public interface PlaybackView extends MvpView {

    void initPlaybackViews();
    void setImageButtonPlayBackground(int drawableId);
    void getAudioFocus();

}
