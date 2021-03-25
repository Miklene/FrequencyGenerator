package com.miklene.frequencygenerator.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.exceptions.ValueGreaterThan22000;
import com.miklene.frequencygenerator.exceptions.ValueLowerThanOneException;
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
            validateValue("from", from);
        } catch (Exception e) {
            error = true;
        }
        try {
            validateValue("to", to);
        } catch (Exception e) {
            error = true;
        }
        if (!error)
            try {
                validateFromValueGreaterToValue();
            } catch (Exception e) {
                error = true;
            }
        if (!error) {
            saveRangeFrom(from);
            saveRangeTo(to);
            saveRange(from, to);
            getViewState().closeDialog();
        }
    }

    private void validateValue(String field, String text) throws Exception {
        boolean error = false;
        int value;
        try {
            value = getValueFromString(text);
            saveValue(field, value);
            validateValueLowerThan1(value);
            validateValueGreaterThan22000(value);
        } catch (NumberFormatException e) {
            showError(field, R.string.error_empty_field);
            error = true;
        } catch (ValueLowerThanOneException e) {
            showError(field, R.string.error_lower_one);
            error = true;
        } catch (ValueGreaterThan22000 valueGreaterThan22000) {
            showError(field, R.string.error_greater_22000);
            error = true;
        }
        if (error)
            throw new Exception();
    }

    private int getValueFromString(String value) throws NumberFormatException {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException();
        }
    }

    private void validateValueLowerThan1(int value) throws ValueLowerThanOneException {
        if (value < 1)
            throw new ValueLowerThanOneException();
    }

    private void validateValueGreaterThan22000(int value) throws ValueGreaterThan22000 {
        if (value > 22000)
            throw new ValueGreaterThan22000();
    }

    private void validateFromValueGreaterToValue() throws Exception {
        if (from > to) {
            getViewState().showErrorFieldFrom(R.string.error_from_less_to);
            getViewState().showErrorFieldTo(R.string.error_to_greater_from);
            throw new Exception();
        }
    }

    private void saveValue(String field, int value) {
        if (field.equals("from"))
            from = value;
        if (field.equals("to"))
            to = value;
    }

    private void showError(String field, int errorId) {
        if (field.equals("from"))
            getViewState().showErrorFieldFrom(errorId);
        if (field.equals("to"))
            getViewState().showErrorFieldTo(errorId);
    }

   /* private void getValuesFromStrings(String from, String to) throws Exception {
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
    }*/

   /* private void validateValues() throws Exception {
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
    }*/



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
