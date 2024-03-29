package com.miklene.frequencygenerator.repository;

import android.content.SharedPreferences;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;

public class SettingsPreferencesRepository implements SettingsRepository{

    private final SharedPreferences preferences;

    private static final String PREFS_SCALE = "Scale";
    private static final String PREFS_RANGE_FROM = "Range_from";
    private static final String PREFS_RANGE_TO = "Range_to";
    private static final String PREFS_RANGE = "range";

    private static Subject<String> rangeSubject;
    private static Subject<String> rangeFromSubject;
    private static Subject<String> rangeToSubject;
    private static Subject<String> scaleSubject;

    public SettingsPreferencesRepository(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public Subject<String> getRangeSubject() {
        rangeSubject = BehaviorSubject
                .createDefault(loadRange());
        return rangeSubject;
    }

    @Override
    public Subject<String> getRangeFromSubject() {
        rangeFromSubject = BehaviorSubject.createDefault(loadStringRangeFrom());
        return rangeFromSubject;
    }

    @Override
    public Subject<String> getRangeToSubject() {
        rangeToSubject = BehaviorSubject.createDefault(loadStringRangeTo());
        return rangeToSubject;
    }

    @Override
    public Subject<String> getScaleSubject() {
        scaleSubject = BehaviorSubject.createDefault(loadStringScale());
        return scaleSubject;
    }

    @Override
    public void saveScale(String scale) {
        getEditor().putString(PREFS_SCALE, scale).apply();
        if(scaleSubject!=null)
            scaleSubject.onNext(scale);
    }

    @Override
    public Single<String> loadScale() {
        return Single.create(subscriber -> subscriber
                .onSuccess(preferences.getString(PREFS_SCALE, "logarithmic")));
    }

    @Override
    public String loadStringScale() {
       return preferences.getString(PREFS_SCALE, "logarithmic");
    }

    @Override
    public void saveRangeFrom(String range) {
        getEditor().putString(PREFS_RANGE_FROM, range).apply();
        if(rangeFromSubject!=null)
            rangeFromSubject.onNext(range);
    }

    @Override
    public Single<String> loadRangeFrom() {
        return  Single.create(subscriber -> subscriber
                .onSuccess(preferences.getString(PREFS_RANGE_FROM, "1")));
    }

    @Override
    public String loadStringRangeFrom() {
        return preferences.getString(PREFS_RANGE_FROM, "1");
    }

    @Override
    public void saveRangeTo(String range) {
        getEditor().putString(PREFS_RANGE_TO, range).apply();
        if(rangeToSubject!=null)
            rangeToSubject.onNext(range);
    }

    @Override
    public Single<String> loadRangeTo() {
        return Single.create(subscriber -> subscriber
                .onSuccess(preferences.getString(PREFS_RANGE_TO, "22000")));
    }

    @Override
    public String loadStringRangeTo() {
        return preferences.getString(PREFS_RANGE_TO, "22000");
    }

    @Override
    public void saveRange(String range) {
        getEditor().putString(PREFS_RANGE, range).apply();
        if(rangeSubject!=null)
            rangeSubject.onNext(range);
    }

    @Override
    public String loadRange() {
        return preferences.getString(PREFS_RANGE, "1 - 22000 Hz");
    }

    private SharedPreferences.Editor getEditor() {
        return preferences.edit();
    }
}
