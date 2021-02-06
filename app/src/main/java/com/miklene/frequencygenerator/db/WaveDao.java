package com.miklene.frequencygenerator.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.miklene.frequencygenerator.wave.Wave;

import java.util.List;

@Dao
public interface WaveDao {

    @Query("SELECT * FROM wavedataclass")
    List<WaveDataClass> getAll();

    @Query("SELECT * FROM wavedataclass WHERE id = :id")
    WaveDataClass getByiD(long id);

    @Insert
    void insert(WaveDataClass wave);

    @Update
    void update(WaveDataClass wave);

    @Delete
    void delete(WaveDataClass wave);
}
