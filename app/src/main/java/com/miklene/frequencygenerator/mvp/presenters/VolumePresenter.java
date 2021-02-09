package com.miklene.frequencygenerator.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.mvp.views.VolumeView;
import com.miklene.frequencygenerator.repository.WaveRepository;

import io.reactivex.rxjava3.disposables.Disposable;

@InjectViewState
public class VolumePresenter extends MvpPresenter<VolumeView> {

    private WaveRepository sharedPrefRepository;
    private Disposable disposable;
    private VolumeInteractor volumeInteractor = VolumeInteractor.getInstance();

    public VolumePresenter(WaveRepository sharedPrefRepository) {
        this.sharedPrefRepository = sharedPrefRepository;
        disposable = volumeInteractor.getVolume().subscribe(this::setVolume);
    }

    public void initVolume() {
        int volume = (sharedPrefRepository.loadVolume());
        setVolume(volume);
        getViewState().setSeekBarVolumeProgress(volume);
    }

    public void seekBarVolumeProgressChanged(int volume) {
        sharedPrefRepository.saveVolume(volume);
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

    public void initVolumeElements() {
     /*   getViewState().setImageButtonVolumeSrc(getVolumeSrc(volume));
        getViewState().setTextViewVolumeValue(getStringValueOfVolume(volume));
        getViewState().setSeekBarVolumeProgress(volume);*/
    }

    @Override
    public void onDestroy(){
        disposable.dispose();
    }
}
