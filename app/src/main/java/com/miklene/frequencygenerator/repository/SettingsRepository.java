package com.miklene.frequencygenerator.repository;

import android.content.SharedPreferences;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.subjects.Subject;

public interface SettingsRepository {

    Subject<String> getRangeSubject();

    void saveScale(String scale);
    Single<String> loadScale();
    void saveRangeFrom(String range);
    Single<String> loadRangeFrom();
    String loadStringRangeFrom();
    void saveRangeTo(String range);
    Single<String> loadRangeTo();
    String loadStringRangeTo();
    void saveRange(String range);
    String loadRange();
}
