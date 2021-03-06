package com.miklene.frequencygenerator.activity;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.player.PlayerState;
import com.miklene.frequencygenerator.player.WavePlayer;
import com.miklene.frequencygenerator.wave.SineWave;
import com.miklene.frequencygenerator.wave.Wave;

@InjectViewState
public class MainPresenter extends MvpPresenter<PlaybackView> {

    private WavePlayer wavePlayer = WavePlayer.getInstance();

    public MainPresenter() {
    }

    public void onImageButtonPlayClicked(){
        Wave wave = new SineWave(200,100);
        PlayerState state = wavePlayer.getState();
        if(state.equals(PlayerState.OFF)) {
            getViewState().setImageButtonPlayBackground(R.drawable.ic_baseline_stop_circle_24);
            wavePlayer.play(wave);
            return;
        }
        if(state.equals(PlayerState.ON) ||
                state.equals(PlayerState.START)){
            wavePlayer.stop();
            getViewState().setImageButtonPlayBackground(R.drawable.ic_baseline_play_circle_filled_24);
        }
    }

    public void onImageButtonIncreaseClicked(){
       /* int userValue = getViewState().getUserSeekBarValue();
        if(userValue+1<22000) {
            getViewState().setEditTextValue(userValue + 1);
            return;
        }*/
        getViewState().setEditTextValue(22000);
    }

    public void onImageButtonDecreaseClicked(){
      /*  int userValue = getViewState().getUserSeekBarValue();
        if(userValue-1>0) {
            getViewState().setEditTextValue(userValue - 1);
            return;
        }*/
        getViewState().setEditTextValue(1);
    }


}
