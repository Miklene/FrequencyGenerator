package com.miklene.frequencygenerator.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.mvp.views.SingleFrequencyView;
import com.miklene.frequencygenerator.player.PlayerState;
import com.miklene.frequencygenerator.player.WavePlayer;
import com.miklene.frequencygenerator.wave.SineWave;
import com.miklene.frequencygenerator.wave.Wave;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;

@InjectViewState
public class SingleFrequencyPresenter extends MvpPresenter<SingleFrequencyView> {

    private WavePlayer wavePlayer = WavePlayer.getInstance();
    private float frequency = 200;
    private int volume = 100;
    private float lastFrequency;
    private long repeats = 0;
    private int step = 1;
    private DisposableObserver<Long> disposable;

    public SingleFrequencyPresenter() {
    }

    public void onEditTextTextChanges(String text) {
        text = text.replace(',', '.');
        try {
            frequency = Float.parseFloat(text);
            if (!text.equals(formatStringValue(frequency)))
                if (lastFrequency == frequency) {
                    setEditTextValue(frequency);
                    return;
                }
            if (lastFrequency == frequency) {
                return;
            }
            if (frequency > 22000)
                frequency = 22000;
            if (frequency < 1)
                frequency = 1;
        } catch (Exception e) {
            frequency = 1;
            getViewState().setSeekBarProgress(calculateSeekBarProgress(frequency));
            setEditTextValue(frequency);
            lastFrequency = frequency;
        }
        getViewState().setSeekBarProgress(calculateSeekBarProgress(frequency));
        setEditTextValue(frequency);
        lastFrequency = frequency;
    }

    public void onImageButtonPlayClicked() {
        Wave wave = new SineWave(200, 100);
        PlayerState state = wavePlayer.getState();
        if (state.equals(PlayerState.OFF)) {
            getViewState().setImageButtonPlayBackground(R.drawable.ic_baseline_stop_circle_24);
            wavePlayer.play(wave);
            return;
        }
        if (state.equals(PlayerState.ON) ||
                state.equals(PlayerState.START)) {
            wavePlayer.stop();
            getViewState().setImageButtonPlayBackground(R.drawable.ic_baseline_play_circle_filled_24);
        }
    }

    public void seekBarVolumeProgressChanged(int progress) {
        volume = progress;
        getViewState().setImageButtonVolumeSrc(getVolumeSrc(volume));
        getViewState().setTextViewVolumeValue(getStringValueOfVolume(volume));
    }

    private int getVolumeSrc(int volumeValue) {
        int resId = R.drawable.blue_rectangle;
        if (volumeValue == 0)
            resId = R.drawable.ic_baseline_volume_off_24;
        if (volumeValue <= 50 && volumeValue > 0)
            resId = R.drawable.ic_baseline_volume_down_24;
        if (volumeValue > 50)
            resId = R.drawable.ic_baseline_volume_up_24;
        return resId;
    }

    private String getStringValueOfVolume(int volumeValue){
        return volumeValue + "%";
    }

    public void initVolumeElements() {
     /*   getViewState().setImageButtonVolumeSrc(getVolumeSrc(volume));
        getViewState().setTextViewVolumeValue(getStringValueOfVolume(volume));
        getViewState().setSeekBarVolumeProgress(volume);*/
    }

    public void seekBarProgressChanged(int progress) {
        if (progress == 14425215)
            frequency = 22000.00f;
        else
            frequency = calculateFrequency(progress);
        setEditTextValue(frequency);
        //getViewState().setEditTextValue(String.valueOf(frequency));
    }

    private void setEditTextValue(float frequency) {
        getViewState().setEditTextValue(formatStringValue(frequency));
    }

    private float calculateFrequency(int seekBarProgress) {
        float value = (float) (Math.pow(2, (seekBarProgress / 1000000d)));
        double scale = Math.pow(10, 2);
        double result = Math.round(value * scale) / scale;
        return (float) result;
        //return (float) (Math.pow(2, (seekBarProgress / 1000000d)));
    }

    private String formatStringValue(float value) {
        return String.format(Locale.getDefault(),"%.2f", value);
    }

    private float formatFloatValue(float value) {
        double scale = Math.pow(10, 2);
        double res = Math.round(value * scale) / scale;
        String result = String.format(Locale.getDefault(),"%.2f", res);
        return Float.parseFloat(result);
    }

    private int calculateSeekBarProgress(double value) {
        return (int) ((Math.log(value) / Math.log(2)) * 1000000);
    }

    public void onImageButtonIncreaseDown() {
        repeats = 0;
        Observable<Long> observable = onLongTouchObservable();
        disposable = new DisposableObserver<Long>() {
            @Override
            public void onNext(@NonNull Long aLong) {
                if (repeats == 0)
                    getViewState().vibrate();
                repeats++;
                frequency += step;
                if (frequency > 22000)
                    frequency = 22000;
                setEditTextValue(frequency);
                getViewState().setSeekBarProgress(calculateSeekBarProgress(frequency));
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
        observable.subscribe(disposable);
    }

    public void onImageButtonIncreaseUp() {
        if (repeats == 0) {
            frequency += step;
            if (frequency > 22000)
                frequency = 22000;
        }
        disposable.dispose();
        setEditTextValue(frequency);
        getViewState().setSeekBarProgress(calculateSeekBarProgress(frequency));
    }

    public void onImageButtonDecreaseDown() {
        repeats = 0;
        Observable<Long> observable = onLongTouchObservable();
        disposable = new DisposableObserver<Long>() {
            @Override
            public void onNext(@NonNull Long aLong) {
                if (repeats == 0)
                    getViewState().vibrate();
                repeats++;
                frequency -= step;
                if (frequency < 1)
                    frequency = 1;
                setEditTextValue(frequency);
                getViewState().setSeekBarProgress(calculateSeekBarProgress(frequency));
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
        observable.subscribe(disposable);
    }

    public void onImageButtonDecreaseUp() {
        if (repeats == 0) {
            frequency -= step;
            if (frequency < 1)
                frequency = 1;
        }
        disposable.dispose();
        setEditTextValue(frequency);
        getViewState().setSeekBarProgress(calculateSeekBarProgress(frequency));
    }

    private Observable<Long> onLongTouchObservable() {
        return Observable.interval(1000, 100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }


}
