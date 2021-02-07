package com.miklene.frequencygenerator.activity.shared_pref;

import com.arellomobile.mvp.MvpView;

public interface SharedPrefView extends MvpView {
    void setSeekBarFrequencyProgress(int progress);
    void setEditTextValue(float frequency);
    void setSpinnerWaveType();
    void setVolumeValue();
}
