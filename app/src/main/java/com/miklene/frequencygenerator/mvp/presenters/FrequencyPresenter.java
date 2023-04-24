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
    private final int step = 1;
    private Disposable longTouchDisposable;
    private final Disposable scaleDisposable;
    private final Disposable rangeFromDisposable;
    private final Disposable rangeToDisposable;

    public FrequencyPresenter(WaveRepository sharedPrefRepository, SettingsRepository settingsRepository) {
        this.sharedPrefRepository = sharedPrefRepository;
        this.settingsRepository = settingsRepository;
        sharedPrefRepository.loadFrequency()
                .subscribeOn(Schedulers.io())
                .doOnSuccess(fr -> frequency = fr)
                .subscribe();
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

    private void saveAndDisplayFrequency(float frequency) {
        saveFrequency(frequency)
                .doOnComplete(() -> {
                    setEditTextFrequency(frequency);
                    setSeekBarFrequency(calculateSeekBarProgress(frequency));
                })
                .subscribe();
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
        saveAndDisplayFrequency(frequency);
    }

    private float getFrequency(String text) throws Exception {
        try {
            return Float.parseFloat(text);
        } catch (Exception e) {
            throw new Exception();
        }
    }

    public void onImageButtonIncreaseLongClick() {
        getViewState().vibrate();
        longTouchDisposable = onLongTouchObservable()
                .doOnNext(l -> {
                    frequency += step;
                    if (frequency > 22000)
                        frequency = 22000;
                    saveAndDisplayFrequency(frequency);
                })
                .subscribe();
    }

    public void onImageButtonIncreaseUp() {
        if (longTouchDisposable != null) {
            if (!longTouchDisposable.isDisposed()) {
                longTouchDisposable.dispose();
                return;
            }
        }
        frequency += step;
        if (frequency > 22000)
            frequency = 22000;
        saveAndDisplayFrequency(frequency);
    }

    public void onImageButtonDecreaseLongClick() {
        getViewState().vibrate();
        if (longTouchDisposable != null)
            longTouchDisposable.dispose();
        longTouchDisposable = onLongTouchObservable()
                .doOnNext(l -> {
                    frequency -= step;
                    if (frequency < 1)
                        frequency = 1;
                    saveAndDisplayFrequency(frequency);
                })
                .subscribe();
    }

    public void onImageButtonDecreaseUp() {
        if (longTouchDisposable != null) {
            if (!longTouchDisposable.isDisposed()) {
                longTouchDisposable.dispose();
                return;
            }
        }
        frequency -= step;
        if (frequency < 1)
            frequency = 1;
        saveAndDisplayFrequency(frequency);
    }

    private Observable<Long> onLongTouchObservable() {
        return Observable.interval(0, 100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (longTouchDisposable != null)
            longTouchDisposable.dispose();
        if (scaleDisposable != null)
            scaleDisposable.dispose();
        if (rangeFromDisposable != null)
            rangeFromDisposable.dispose();
        if (rangeToDisposable != null)
            rangeToDisposable.dispose();
    }
}
