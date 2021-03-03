package com.miklene.frequencygenerator.mvp.views;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpView;

public interface FrequencyView extends MvpView {

    void setEditTextFrequencyText(String frequency);
    void initFrequencyViews();
}
