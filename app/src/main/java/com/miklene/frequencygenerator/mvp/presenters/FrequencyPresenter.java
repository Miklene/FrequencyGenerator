package com.miklene.frequencygenerator.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.miklene.frequencygenerator.mvp.views.FrequencyView;
import com.miklene.frequencygenerator.repository.WaveRepository;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@InjectViewState
public class FrequencyPresenter extends MvpPresenter<FrequencyView> {

    private final WaveRepository sharedPrefRepository;
    private float frequency;
    private long repeats = 0;
    private final int step = 1;
    private Disposable buttonDisposable;

    public FrequencyPresenter(WaveRepository sharedPrefRepository) {
        this.sharedPrefRepository = sharedPrefRepository;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().initFrequencyViews();
        setFrequency();
    }

    private void setFrequency() {
        sharedPrefRepository.loadFrequency()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(frequency -> {
                    setSeekBarFrequency(frequency);
                    setEditTextFrequency(frequency);
                })
                .subscribe();
    }

    private void setSeekBarFrequency(float frequency) {
        getViewState().setSeekBarFrequencyProgress(calculateSeekBarProgress(frequency));
    }

    private void setEditTextFrequency(float frequency) {
        getViewState().setEditTextFrequencyText(formatStringValue(frequency));
    }

    public void onSeekBarFrequencyChanged(int progress) {
        if (progress == 14425215)
            frequency = 22000.00f;
        else
            frequency = calculateFrequency(progress);
        saveFrequency(frequency)
                .doOnComplete(() -> setEditTextFrequency(frequency))
                .subscribe();
    }

    private Completable saveFrequency(float frequency) {
        return Completable.fromAction(() -> sharedPrefRepository.saveFrequency(frequency))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private float calculateFrequency(int seekBarProgress) {
        float value = (float) (Math.pow(2, (seekBarProgress / 1000000d)));
        double scale = Math.pow(10, 2);
        double result = Math.round(value * scale) / scale;
        return (float) result;
    }

    private String formatStringValue(float value) {
        return String.format(Locale.getDefault(), "%.2f", value);
    }

    public void onEditTextFrequencyTextChanges(String text) {
        //text = text.replace(',', '.');
        try {
            frequency = Float.parseFloat(text);
        } catch (Exception e) {
            frequency = 1;
            saveFrequency(frequency)
                    .doOnComplete(() -> {
                        setEditTextFrequency(frequency);
                        setSeekBarFrequency(frequency);
                    })
                    .subscribe();
        }
        if (frequency > 22000) {
            frequency = 22000;
            saveFrequency(frequency)
                    .doOnComplete(() -> setEditTextFrequency(frequency))
                    .subscribe();
        }
        if (frequency < 1) {
            frequency = 1;
            saveFrequency(frequency)
                    .doOnComplete(() -> setEditTextFrequency(frequency))
                    .subscribe();
        }
        saveFrequency(frequency)
                .doOnComplete(() -> setSeekBarFrequency(frequency))
                .subscribe();
    }

    private int calculateSeekBarProgress(double value) {
        return (int) ((Math.log(value) / Math.log(2)) * 1000000);
    }

    public void onImageButtonIncreaseDown() {
        repeats = 0;
        buttonDisposable = onLongTouchObservable()
                .doOnNext(l -> {
                    if (repeats == 0)
                        getViewState().vibrate();
                    repeats++;
                    frequency += step;
                    if (frequency > 22000)
                        frequency = 22000;
                    saveFrequency(frequency)
                            .doOnComplete(() -> {
                                setEditTextFrequency(frequency);
                                setSeekBarFrequency(frequency);
                            })
                            .subscribe();
                })
                .subscribe();
    }

    public void onImageButtonIncreaseUp() {
        buttonDisposable.dispose();
        if (repeats == 0) {
            frequency += step;
            if (frequency > 22000)
                frequency = 22000;
        }
        saveFrequency(frequency)
                .doOnComplete(() -> setEditTextFrequency(frequency))
                .subscribe();
    }

    public void onImageButtonDecreaseDown() {
        repeats = 0;
        buttonDisposable = onLongTouchObservable()
                .doOnNext(l -> {
                    if (repeats == 0)
                        getViewState().vibrate();
                    repeats++;
                    frequency -= step;
                    if (frequency < 1)
                        frequency = 1;
                    saveFrequency(frequency)
                            .doOnComplete(() -> {
                                setEditTextFrequency(frequency);
                                setSeekBarFrequency(frequency);
                            })
                            .subscribe();
                })
                .subscribe();
    }

    public void onImageButtonDecreaseUp() {
        buttonDisposable.dispose();
        if (repeats == 0) {
            frequency -= step;
            if (frequency < 1)
                frequency = 1;
        }
        saveFrequency(frequency)
                .doOnComplete(() -> setEditTextFrequency(frequency))
                .subscribe();
    }

    private Observable<Long> onLongTouchObservable() {
        return Observable.interval(1000, 100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        buttonDisposable.dispose();
    }
}
