package com.miklene.frequencygenerator.db;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.miklene.frequencygenerator.wave.Wave;
import com.miklene.frequencygenerator.wave.WaveType;
import com.miklene.frequencygenerator.wave.WaveTypeConverter;

@Database(entities = {WaveDataClass.class}, version = 1)
public abstract class WaveDatabase extends RoomDatabase {

    public abstract WaveDao waveDao();
}
