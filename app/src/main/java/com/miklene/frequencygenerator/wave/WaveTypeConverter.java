package com.miklene.frequencygenerator.wave;

import androidx.room.TypeConverter;

public class WaveTypeConverter {
    @TypeConverter
    public WaveType toWaveType(String waveType){
        return WaveType.valueOf(waveType);
    }

    @TypeConverter
    public String toWaveType(WaveType waveType){
        return waveType.toString();
    }
}
