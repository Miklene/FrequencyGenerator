package com.miklene.frequencygenerator.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.miklene.frequencygenerator.wave.WaveType;
import com.miklene.frequencygenerator.wave.WaveTypeConverter;

@Entity
public class WaveDataClass {

    @PrimaryKey
    public long id=0;
    @TypeConverters({WaveTypeConverter.class})
    public WaveType type;
    public float frequency;
    public double amplitude;
    public double rightChannel;
    public double leftChannel;

    public WaveDataClass(float frequency, double amplitude, double rightChannel, double leftChannel) {
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.rightChannel = rightChannel;
        this.leftChannel = leftChannel;
    }
}
