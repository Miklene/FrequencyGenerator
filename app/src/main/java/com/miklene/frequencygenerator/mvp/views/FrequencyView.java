package com.miklene.frequencygenerator.mvp.views;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpView;

public interface FrequencyView extends MvpView {

    void initFrequencyViews();
    void setEditTextFrequencyText(String frequency);
    void setEditTextFrequencyError(int textId);
    void setSeekBarFrequencyProgress(int progress);
    void setSeekBarMax(int progress);
    void vibrate();
}
