package com.miklene.frequencygenerator.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.mvp.views.FrequencyView;
import com.miklene.frequencygenerator.repository.SettingsRepository;
import com.miklene.frequencygenerator.repository.WaveRepository;
import com.miklene.frequencygenerator.util.FrequencyCounter;
import com.miklene.frequencygenerator.util.FrequencyCounterFactory;

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
    private final SettingsRepository settingsRepository;
    private FrequencyCounter counter;
    private float frequency;
    private long repeats = 0;
    private final int step = 1;
    private Disposable buttonDisposable;
    private final Disposable scaleDisposable;
    private final Disposable rangeFromDisposable;
    private final Disposable rangeToDisposable;

    public FrequencyPresenter(WaveRepository sharedPrefRepository, SettingsRepository settingsRepository) {
        this.sharedPrefRepository = sharedPrefRepository;
        this.settingsRepository = settingsRepository;
        counter = FrequencyCounterFactory.getFrequencyCounter(
                settingsRepository.loadStringScale(),
                Integer.parseInt(settingsRepository.loadStringRangeFrom()),
                Integer.parseInt(settingsRepository.loadStringRangeTo()));
        scaleDisposable = settingsRepository.getScaleSubject()
                .doOnNext(s -> counter = FrequencyCounterFactory.getFrequencyCounter(
                        s,
                        Integer.parseInt(settingsRepository.loadStringRangeFrom()),
                        Integer.parseInt(settingsRepository.loadStringRangeTo()))
                )
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(r -> {
                    getViewState().setSeekBarMax(counter.countSeekBarMax());
                    setFrequency();
                })
                .subscribe();
        rangeFromDisposable = settingsRepository.getRangeFromSubject()
                .map(Integer::parseInt)
                .doOnNext(r -> counter.setValueFrom(r))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(r -> {
                    getViewState().setSeekBarMax(counter.countSeekBarMax());
                    setFrequency();
                })
                .subscribe();
        rangeToDisposable = settingsRepository.getRangeToSubject()
                .map(Integer::parseInt)
                .doOnNext(r -> counter.setValueTo(r))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(r -> {
                    getViewState().setSeekBarMax(counter.countSeekBarMax());
                    setFrequency();
                })
                .subscribe();
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().initFrequencyViews();
        setFrequency();
    }

    private void setFrequency() {
        sharedPrefRepository.loadFrequency().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(frequency -> counter.validateFrequency(frequency))
                .doOnSuccess(frequency -> {
                    setSeekBarFrequency(calculateSeekBarProgress(frequency));
                    setEditTextFrequency(frequency);
                })
                .subscribe();
    }

    private void setSeekBarFrequency(int progress) {
        getViewState().setSeekBarFrequencyProgress(progress);
    }

    private int calculateSeekBarProgress(double value) {
        return counter.countProgress(value);//int) ((Math.log(value) / Math.log(2)) * 1000000);
    }

    private void setEditTextFrequency(float frequency) {
        getViewState().setEditTextFrequencyText(formatStringValue(frequency));
    }

    public void onSeekBarFrequencyChanged(int progress) {
        frequency = (float) counter.countFrequency(progress);
        saveFrequency(frequency)
                .doOnComplete(() -> setEditTextFrequency(frequency))
                .subscribe();
    }

    private String formatStringValue(float value) {
        return String.format(Locale.getDefault(), "%.2f", value);
    }

    private Completable saveFrequency(float frequency) {
        return Completable.fromAction(() -> sharedPrefRepository.saveFrequency(frequency))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

  /*  private float calculateFrequency(int seekBarProgress) {
        float value = (float) (Math.pow(2, (seekBarProgress / 1000000d)));
        double scale = Math.pow(10, 2);
        double result = Math.round(value * scale) / scale;
        return (float) result;
    }*/

    public void onEditTextFrequencyTextChanges(String text) {
        text = text.replace(',', '.');  //НК
        try {
            frequency = getFrequency(text);
        } catch (Exception e) {
            getViewState().setEditTextFrequencyError(R.string.incorrectValue);
            return;
        }
        frequency = counter.validateFrequency(frequency);
        saveFrequency(frequency)
                .doOnComplete(() -> setEditTextFrequency(frequency))
                .doOnComplete(() -> setSeekBarFrequency(calculateSeekBarProgress(frequency)))
                .subscribe();
    }

    private float getFrequency(String text) throws Exception {
        try {
            return Float.parseFloat(text);
        } catch (Exception e) {
            throw new Exception();
        }
    }

    public void onImageButtonIncreaseDown() {

        if (buttonDisposable != null)
            buttonDisposable.dispose();
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
                                setSeekBarFrequency(calculateSeekBarProgress(frequency));
                            })
                            .subscribe();
                })
                .subscribe();
    }

    public void onImageButtonIncreaseLongClick(){
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
                                setSeekBarFrequency(calculateSeekBarProgress(frequency));
                            })
                            .subscribe();
                })
                .subscribe();
    }

    public void onImageButtonIncreaseUp() {
        if (buttonDisposable != null)
            buttonDisposable.dispose();
        if (repeats == 0) {
            frequency += step;
            if (frequency > 22000)
                frequency = 22000;
        }
        repeats = 0;
        saveFrequency(frequency)
                .doOnComplete(() -> {
                    setEditTextFrequency(frequency);
                    setSeekBarFrequency(calculateSeekBarProgress(frequency));
                })
                .subscribe();
    }

    public void onImageButtonDecreaseDown() {
        repeats = 0;
        if (buttonDisposable != null)
            buttonDisposable.dispose();
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
                                setSeekBarFrequency(calculateSeekBarProgress(frequency));
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
                .doOnComplete(() -> {
                    setEditTextFrequency(frequency);
                    setSeekBarFrequency(calculateSeekBarProgress(frequency));
                })
                .subscribe();
    }

    private Observable<Long> onLongTouchObservable() {
        return Observable.interval(0, 100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (buttonDisposable != null)
            buttonDisposable.dispose();
        if (scaleDisposable != null)
            scaleDisposable.dispose();
        if (rangeFromDisposable != null)
            rangeFromDisposable.dispose();
        if (rangeToDisposable != null)
            rangeToDisposable.dispose();
    }
}
