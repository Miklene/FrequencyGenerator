package com.miklene.frequencygenerator.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.miklene.frequencygenerator.mvp.views.RangeView;
import com.miklene.frequencygenerator.repository.SettingsRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@InjectViewState
public class RangePresenter extends MvpPresenter<RangeView> {

    private SettingsRepository repository;
    private final String defaultRageFrom = "1";
    private final String defaultRageTo = "22000";

    public RangePresenter(SettingsRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().initRangeViews();
        setRangeFrom();
        setRangeTo();
    }

    private void setRangeFrom() {
        repository.loadRangeFrom()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(getViewState()::setRangeFrom)
                .subscribe();
    }

    private void setRangeTo() {
        repository.loadRangeTo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(getViewState()::setRangeTo)
                .subscribe();
    }

    public void onNeutralButtonClicked() {
        getViewState().setRangeFrom(defaultRageFrom);
        getViewState().setRangeTo(defaultRageTo);
        //saveRange(defaultRageFrom, defaultRageTo);
    }

    public void saveRangeFrom(String range) {
        Completable.fromAction(() -> repository.saveRangeFrom(range))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void saveRangeTo(String range) {
        Completable.fromAction(() -> repository.saveRangeTo(range))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void saveRange(String from, String to) {
        String builder = from + " - " + to + " Hz";
        Completable.fromAction(() -> repository.saveRange(builder))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
