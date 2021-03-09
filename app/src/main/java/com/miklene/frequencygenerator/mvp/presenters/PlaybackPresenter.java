package com.miklene.frequencygenerator.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.mvp.views.PlaybackView;
import com.miklene.frequencygenerator.player.PlayerState;
import com.miklene.frequencygenerator.player.WavePlayer;
import com.miklene.frequencygenerator.repository.WaveRepository;
import com.miklene.frequencygenerator.wave.SimpleWaveFactory;
import com.miklene.frequencygenerator.wave.SineWave;
import com.miklene.frequencygenerator.wave.Wave;
import com.miklene.frequencygenerator.wave.WaveType;

import io.reactivex.rxjava3.disposables.Disposable;

@InjectViewState
public class PlaybackPresenter extends MvpPresenter<PlaybackView> {

    private final WaveRepository sharedPrefRepository;
    private final WavePlayer wavePlayer = WavePlayer.getInstance();
    private Disposable frequencyDisposable;
    private Disposable volumeDisposable;
    private Disposable rightChannelDisposable;
    private Disposable leftChannelDisposable;
    private Disposable waveTypeDisposable;
    private Wave wave = new SineWave(200, 1d, 1d, 1d);


    public PlaybackPresenter(WaveRepository sharedPrefRepository) {
        this.sharedPrefRepository = sharedPrefRepository;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().initPlaybackViews();
    }

    public void onImageButtonPlayClicked() {
        PlayerState state = wavePlayer.getState();
        if (state.equals(PlayerState.OFF)) {
            frequencyDisposable = sharedPrefRepository.getFrequencySubject().subscribe(this::changeFrequency);
            volumeDisposable = sharedPrefRepository.getVolumeSubject().subscribe(this::changeVolume);
            rightChannelDisposable = sharedPrefRepository.getRightChannelSubject().subscribe(this::changeRightChannel);
            leftChannelDisposable = sharedPrefRepository.getLeftChannelSubject().subscribe(this::changeLeftChannel);
            waveTypeDisposable = sharedPrefRepository.getWaveTypeSubject().subscribe(this::changeWaveType);
            getViewState().setImageButtonPlayBackground(R.drawable.ic_baseline_stop_circle_24);
            wavePlayer.play(wave);
            return;
        }
        if (state.equals(PlayerState.ON) || state.equals(PlayerState.START)) {
            wavePlayer.stop();
            frequencyDisposable.dispose();
            volumeDisposable.dispose();
            rightChannelDisposable.dispose();
            leftChannelDisposable.dispose();
            waveTypeDisposable.dispose();
            getViewState().setImageButtonPlayBackground(R.drawable.ic_baseline_play_circle_filled_24);
        }
    }

    private void changeFrequency(float frequency) {
        wave.setFrequency(frequency);
    }

    private void changeVolume(int volume) {
        wave.setVolume(volume / 100d);
    }

    private void changeRightChannel(int rightChannel) {
        wave.setRight(rightChannel / 100d);
    }

    private void changeLeftChannel(int leftChannel) {
        wave.setLeft(leftChannel / 100d);
    }

    private void changeWaveType(String waveType) {
        wave = SimpleWaveFactory.createWave(WaveType.valueOf(waveType), wave.getFrequency(),
                wave.getVolume(), wave.getLeft(), wave.getRight());
        if(wavePlayer.isPlayed()) {
            wavePlayer.stop();
            while(wavePlayer.isPlayed());
                wavePlayer.play(wave);
        }
    }
}
