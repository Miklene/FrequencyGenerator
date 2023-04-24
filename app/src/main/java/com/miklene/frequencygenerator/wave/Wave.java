package com.miklene.frequencygenerator.wave;

import android.util.Log;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;


public abstract class Wave implements RecordParameters {

    long id = 0;
    float frequency;
    double volume;
    double lastVolume;
    double left;
    double lastLeft;
    double right;
    double lastRight;
    double phase;

    public Wave(float frequency, double volume, double left, double right) {
        this.frequency = frequency;
        this.volume = volume;
        lastVolume = volume;
        this.left = left;
        lastLeft = left;
        this.right = right;
        lastRight = right;
    }

    public abstract float[] createBuffer();

    void countPhase() {
        double period = 1d / frequency;
        phase = phase + durationPerSampleRate / period;
        phase -= (int) phase;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public float getFrequency() {
        return frequency;
    }

    public void setVolume(double volume) {
        lastVolume = this.volume;
        this.volume = volume;
    }

    public double getVolume() {
        return volume;
    }

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        lastLeft = this.left;
        this.left = left;
    }

    public double getRight() {
        return right;
    }

    public void setRight(double right) {
        lastRight = this.right;
        this.right = right;
    }

    public double getPhase() {
        return phase;
    }

    public void setPhase(double phase) {
        this.phase = phase;
    }

    @Override
    public String toString() {
        return "Wave";
    }
}
