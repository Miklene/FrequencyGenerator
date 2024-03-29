package com.miklene.frequencygenerator.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.mvp.interactors.VolumeInteractor;
import com.miklene.frequencygenerator.mvp.views.VolumeView;
import com.miklene.frequencygenerator.repository.WaveRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@InjectViewState
public class VolumePresenter extends MvpPresenter<VolumeView> {

    private final WaveRepository sharedPrefRepository;
    private final Disposable volumeInteractorDisposable;
    private final VolumeInteractor volumeInteractor = VolumeInteractor.getInstance();

    public VolumePresenter(WaveRepository sharedPrefRepository) {
        this.sharedPrefRepository = sharedPrefRepository;
        volumeInteractorDisposable = volumeInteractor.getVolume().subscribe(this::setVolume);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().initVolumeViews();
    }

    public void initVolume() {
        sharedPrefRepository.loadVolume()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(volumeInteractor::setVolume)
                .subscribe();
    }

    public void seekBarVolumeProgressChanged(int volume) {
        Completable.fromAction(() -> sharedPrefRepository.saveVolume(volume))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> volumeInteractor.setVolume(volume))
                .subscribe();
    }

    private void setVolume(int volume) {
        getViewState().setSeekBarVolumeProgress(volume);
        getViewState().setImageButtonVolumeSrc(getVolumeSrc(volume));
        getViewState().setTextViewVolumeText(getStringValueOfVolume(volume));
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
    public void onDestroy() {
        volumeInteractorDisposable.dispose();
    }
}
