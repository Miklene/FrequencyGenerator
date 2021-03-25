package com.miklene.frequencygenerator.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.miklene.frequencygenerator.R;
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

    private int from;
    private int to;

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
    }

    public void onPositiveButtonClicked(String from, String to) {
        boolean error = false;
        getViewState().clearErrors();
        try {
            getValuesFromStrings(from, to);
        } catch (Exception e) {
            return;
        }
        try {
            validateValues();
        } catch (Exception e) {
            error = true;
        }
        try {
            validateFromValueGreaterToValue();
        } catch (Exception e) {
            error = true;
        }
        if (!error)
            getViewState().closeDialog();
    }

    private void getValuesFromStrings(String from, String to) throws Exception {
        boolean error = false;
        try {
            this.from = Integer.parseInt(from);
        } catch (Exception e) {
            getViewState().showErrorFieldFrom(R.string.error_empty_field);
            error = true;
        }
        try {
            this.to = Integer.parseInt(to);
        } catch (Exception e) {
            getViewState().showErrorFieldTo(R.string.error_empty_field);
            error = true;
        }
        if (error)
            throw new Exception();
    }

    private void validateValues() throws Exception {
        boolean error = false;
        try {
            validateFromValue(this.from);
        } catch (Exception e) {
            error = true;
        }
        try {
            validateToValue(this.to);
        } catch (Exception e) {
            error = true;
        }
        if (error)
            throw new Exception();
    }

    private void validateFromValue(int value) throws Exception {
        if (value < 1) {
            getViewState().showErrorFieldFrom(R.string.error_lower_one);
            throw new Exception();
        }
        if (value > 22000) {
            getViewState().showErrorFieldFrom(R.string.error_greater_22000);
            throw new Exception();
        }
    }

    private void validateToValue(int value) throws Exception {
        if (value < 1) {
            getViewState().showErrorFieldTo(R.string.error_lower_one);
            throw new Exception();
        }
        if (value > 22000) {
            getViewState().showErrorFieldTo(R.string.error_greater_22000);
            throw new Exception();
        }
    }

    private void validateFromValueGreaterToValue() throws Exception {
        if (from > to) {
            getViewState().showErrorFieldFrom(R.string.error_from_less_to);
            getViewState().showErrorFieldTo(R.string.error_to_greater_from);
            throw new Exception();
        }
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
