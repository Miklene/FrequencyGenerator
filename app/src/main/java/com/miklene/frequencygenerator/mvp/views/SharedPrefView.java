package com.miklene.frequencygenerator.mvp.views;

import com.arellomobile.mvp.MvpView;

public interface SharedPrefView extends MvpView {
    void setSeekBarFrequencyProgress(int progress);
    void setEditTextValue(String value);
    void setSpinnerWaveType();
    void setTextViewVolumeValue(String value);
}
