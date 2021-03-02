package com.miklene.frequencygenerator.mvp.views;

import com.arellomobile.mvp.MvpView;

public interface VolumeView extends MvpView {

    void initVolumeViews();
    void setImageButtonVolumeSrc(int drawableId);
    void setTextViewVolumeValue(String volumeValue);
    void setSeekBarVolumeProgress(int progress);
}
