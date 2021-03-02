package com.miklene.frequencygenerator.mvp.views;

import com.arellomobile.mvp.MvpView;

public interface BalanceView extends MvpView {

    void initBalanceViews();
    void setTextViewRightChannelValue(String value);
    void setTextViewLeftChannelValue(String value);
    void setSeekBarBalanceProgress(int progress);
}
