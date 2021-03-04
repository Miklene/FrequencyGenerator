package com.miklene.frequencygenerator.mvp.presenters;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.mvp.views.PlaybackView;
import com.miklene.frequencygenerator.player.PlayerState;
import com.miklene.frequencygenerator.player.WavePlayer;
import com.miklene.frequencygenerator.repository.WaveRepository;
import com.miklene.frequencygenerator.wave.SineWave;
import com.miklene.frequencygenerator.wave.Wave;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

@InjectViewState
public class PlaybackPresenter extends MvpPresenter<PlaybackView> {

    private final WaveRepository sharedPrefRepository;
    private WavePlayer wavePlayer = WavePlayer.getInstance();
    private Disposable disposable;
    private Disposable disposable2;
    private  Wave wave = new SineWave(200, 100);;

    public PlaybackPresenter(WaveRepository sharedPrefRepository) {
        this.sharedPrefRepository = sharedPrefRepository;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().initPlaybackViews();
        disposable = sharedPrefRepository.getFrequencySubject()
                .subscribe(this::changeFrequency);
        disposable2 = sharedPrefRepository.getVolumeSubject().subscribe(this::changeVolume);
    }

    public void onImageButtonPlayClicked() {
        wave = new SineWave(200, 100);
        PlayerState state = wavePlayer.getState();
        if (state.equals(PlayerState.OFF)) {
            getViewState().setImageButtonPlayBackground(R.drawable.ic_baseline_stop_circle_24);
            wavePlayer.play(wave);
            return;
        }
        if (state.equals(PlayerState.ON) || state.equals(PlayerState.START)) {
            wavePlayer.stop();
            disposable.dispose();
            getViewState().setImageButtonPlayBackground(R.drawable.ic_baseline_play_circle_filled_24);
        }
    }

    private void changeFrequency(float frequency){
        wave.setFrequency(frequency);
    }

    private void changeVolume(int volume){
        wave.setAmplitude(volume/100d);
    }

}
