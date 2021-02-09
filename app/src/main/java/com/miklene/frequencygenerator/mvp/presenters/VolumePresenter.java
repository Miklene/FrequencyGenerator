package com.miklene.frequencygenerator.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.mvp.views.VolumeView;
import com.miklene.frequencygenerator.repository.WaveRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@InjectViewState
public class VolumePresenter extends MvpPresenter<VolumeView> {

    private final WaveRepository sharedPrefRepository;
    private final Disposable disposable;
    private final VolumeInteractor volumeInteractor = VolumeInteractor.getInstance();

    public VolumePresenter(WaveRepository sharedPrefRepository) {
        this.sharedPrefRepository = sharedPrefRepository;
        disposable = volumeInteractor.getVolume().subscribe(this::setVolume);
    }

    public void initVolume() {
        sharedPrefRepository.loadVolume()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(this::setVolume)
                .doOnSuccess(volume->getViewState().setSeekBarVolumeProgress(volume))
                .subscribe();
    }

    public void seekBarVolumeProgressChanged(int volume) {
        Completable.fromAction(()->sharedPrefRepository.saveVolume(volume))
            .subscribeOn(Schedulers.io())
            .subscribe();
        volumeInteractor.setVolume(volume);
        setVolume(volume);
    }

    private void setVolume(int volume) {
        getViewState().setImageButtonVolumeSrc(getVolumeSrc(volume));
        getViewState().setTextViewVolumeValue(getStringValueOfVolume(volume));
    }

    private int getVolumeSrc(int volume) {
        int resId = R.drawable.ic_baseline_volume_off_24;
        if (volume > 0 && volume <= 50)
            resId = R.drawable.ic_baseline_volume_down_24;
        if (volume > 50)
            resId = R.drawable.ic_baseline_volume_up_24;
        return resId;
    }

    private String getStringValueOfVolume(int volumeValue) {
        return volumeValue + "%";
    }

    @Override
    public void onDestroy(){
        disposable.dispose();
    }
}
