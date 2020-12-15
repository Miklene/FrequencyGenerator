package com.miklene.frequencygenerator.player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.miklene.frequencygenerator.wave.RecordParameters;
import com.miklene.frequencygenerator.wave.Wave;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WavePlayer implements RecordParameters {

    private Thread thread;
    private AudioTrack audioTrack;
    private Wave wave;
    private static WavePlayer wavePlayer;
    private boolean play = false;
    private float[] buffer;
    ExecutorService executor;
    AudioPlayer audioPlayer;

    private WavePlayer() {
    }

    public static WavePlayer getInstance() {
        if (wavePlayer == null)
            wavePlayer = new WavePlayer();
        return wavePlayer;
    }

    public void play(Wave wave) {
        if (this.wave != null) {
            if (!(this.wave.equals(wave))) {
                if (audioPlayer.getState().equals(PlayerState.ON) ||
                        (audioPlayer.getState().equals(PlayerState.START)))
                    stop();
                this.wave = wave;
                //executor = Executors.newSingleThreadExecutor();
                audioPlayer = new AudioPlayer();
                executor.submit(audioPlayer);
                play = true;
            }
        } else {
            executor = Executors.newCachedThreadPool();
            this.wave = wave;
            audioPlayer = new AudioPlayer();
            executor.submit(audioPlayer);
            play = true;
        }
    }

    public void stop() {
        play = false;
        //stopWavePlayer();
        //executor.shutdown();
        audioPlayer.setState(PlayerState.END);
        //wave = null;
    }

    public void stopWavePlayer() {
        interrupt(thread);

        if (audioTrack != null) {
            try {
                audioTrack.pause();
            } catch (IllegalStateException e) {
            }
        }

        wait(thread);

        if (audioTrack != null) {
            audioTrack.flush();
            audioTrack.release();
            audioTrack = null;
        }
        thread = null;
        // myPhase=0;
    }

    public boolean isPlayed() {
        try {
            return play;
        } catch (NullPointerException e) {
            return false;
        }
    }

    private static void interrupt(Thread thread) {
        if (thread != null) {
            thread.interrupt();
        }
    }

    private static void wait(Thread thread) {
        /*if (thread != null) {
            try {
                //thread.join();
            } catch (InterruptedException e) {
            }
        }*/
    }

    public PlayerState getState() {
        if (audioPlayer != null)
            return audioPlayer.getState();
        return PlayerState.OFF;
    }

    class AudioPlayer implements Runnable {

        AudioTrack audioTrack;
        private PlayerState state;

        public void setState(PlayerState state) {
            this.state = state;
        }

        public PlayerState getState() {
            return state;
        }

        public AudioPlayer() {
            state = PlayerState.OFF;
        }

        void playAudio() {
            state = PlayerState.START;
            if (state == PlayerState.START) {
                StartPlayback startPlayback = new StartPlayback();
                /*if(SoundEffectsStatus.stereo)
                    prepareAudioTrack(Settings.duration*2);
                else prepareAudioTrack(Settings.duration);*/
                prepareAudioTrack(duration * 2);
                write(startPlayback.createBuffer(wave.createBuffer()));
                play();
                state = PlayerState.ON;
            }
            while (state == PlayerState.ON) {
                write(wave.createBuffer());
            }
            if (state == PlayerState.END) {
                EndPlayback endPlayback = new EndPlayback();
                write(endPlayback.createBuffer(wave.createBuffer()));
                NullPlayback nullPlayback = new NullPlayback();
                write(nullPlayback.createBuffer(wave.createBuffer()));
                audioTrack.stop();
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    Log.i("Exception", e.toString());
                }
                audioTrack.release();
                state = PlayerState.OFF;
                wave = null;
            }
        }

        private void prepareAudioTrack(int length) {
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    RecordParameters.sampleRate, AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_FLOAT,
                    length, AudioTrack.MODE_STREAM);
        }

        private void play() {
            audioTrack.play();
        }

        private void write(float[] buffer) {
            audioTrack.write(buffer, 0, buffer.length, AudioTrack.WRITE_BLOCKING);
        }

        @Override
        public void run() {
            playAudio();
        }
    }
}
