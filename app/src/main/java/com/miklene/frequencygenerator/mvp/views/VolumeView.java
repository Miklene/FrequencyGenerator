package com.miklene.frequencygenerator.mvp.views;

import com.arellomobile.mvp.MvpView;

public interface VolumeView extends MvpView {

    void initViews();
    void setImageButtonVolumeSrc(int drawableId);
    void setTextViewVolumeValue(String volumeValue);
    void setSeekBarVolumeProgress(int progress);
}
