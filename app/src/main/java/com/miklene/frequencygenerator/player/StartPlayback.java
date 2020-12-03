package com.miklene.frequencygenerator.player;

public class StartPlayback {

    public StartPlayback() {

    }

    public float[] createBuffer(float[] buffer) {
        for(int i = 0; i<550; i++)
            buffer[i] *= calculateIncrease(i);
        return buffer;
    }

    private double calculateIncrease(int item){
        if(item<50)
            return 0;
        return (item-50)*0.002;
    }
}
