package com.miklene.frequencygenerator.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.mvp.views.PlaybackView;
import com.miklene.frequencygenerator.player.PlayerState;
import com.miklene.frequencygenerator.player.WavePlayer;
import com.miklene.frequencygenerator.wave.SineWave;
import com.miklene.frequencygenerator.wave.Wave;

@InjectViewState
public class PlaybackPresenter extends MvpPresenter<PlaybackView> {

    private WavePlayer wavePlayer = WavePlayer.getInstance();

    public PlaybackPresenter() {
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().initPlaybackViews();
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
}
