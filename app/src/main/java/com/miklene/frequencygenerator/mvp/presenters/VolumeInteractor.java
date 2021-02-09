package com.miklene.frequencygenerator.mvp.presenters;

import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;

public class VolumeInteractor {
    private static VolumeInteractor instance;
    int volumeValue = 0;
    private Subject<Integer> volume = BehaviorSubject.createDefault(volumeValue);


    public Subject<Integer> getVolume(){
        return volume;
    }

    public static VolumeInteractor getInstance(){
        if (instance==null)
            instance=new VolumeInteractor();
        return instance;
    }

    public void setVolume(int volumeValue){
        this.volumeValue = volumeValue;
        volume.onNext(volumeValue);
    }
}
