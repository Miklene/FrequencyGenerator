package com.miklene.frequencygenerator.mvp.views;

import com.arellomobile.mvp.MvpView;

public interface RangeView extends MvpView {

    void initRangeViews();
    void setRangeFrom(String range);
    void setRangeTo(String range);
}
