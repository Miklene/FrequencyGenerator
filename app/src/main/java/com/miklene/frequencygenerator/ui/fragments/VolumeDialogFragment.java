package com.miklene.frequencygenerator.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.arellomobile.mvp.MvpAppCompatDialogFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.jakewharton.rxbinding4.view.RxView;
import com.jakewharton.rxbinding4.widget.RxSeekBar;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.mvp.presenters.BalancePresenter;
import com.miklene.frequencygenerator.mvp.presenters.PlaybackPresenter;
import com.miklene.frequencygenerator.mvp.presenters.VolumePresenter;
import com.miklene.frequencygenerator.mvp.views.BalanceView;
import com.miklene.frequencygenerator.mvp.views.PlaybackView;
import com.miklene.frequencygenerator.mvp.views.VolumeView;
import com.miklene.frequencygenerator.databinding.FragmentDialogVolumeBinding;
import com.miklene.frequencygenerator.repository.PreferencesRepository;
import com.miklene.frequencygenerator.repository.WaveRepository;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class VolumeDialogFragment extends MvpAppCompatDialogFragment implements VolumeView,
        BalanceView, PlaybackView {

    private static final String PREFS_FILE = "Wave";
    private SharedPreferences preferences;
    private WaveRepository repository;

    @InjectPresenter
    VolumePresenter volumePresenter;

    @ProvidePresenter
    VolumePresenter provideVolumePresenter() {
        return new VolumePresenter(getRepository());
    }

    @InjectPresenter
    PlaybackPresenter playbackPresenter;

    @ProvidePresenter
    PlaybackPresenter providePlaybackPresenter() {
        return new PlaybackPresenter(getRepository());
    }

    @InjectPresenter
    BalancePresenter balancePresenter;

    @ProvidePresenter
    BalancePresenter provideBalancePresenter() {
        return new BalancePresenter(getRepository());
    }

    private WaveRepository getRepository() {
        if (preferences == null) {
            preferences = this.requireActivity()
                    .getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
            repository = new PreferencesRepository(preferences);
        }
        return repository;
    }

    @Override
    public void initPlaybackViews() {

    }

    @Override
    public void setImageButtonPlayBackground(int drawableId) {

    }

    public interface VolumeListener {
        void confirmButtonClicked();

        void cancelButtonClicked();
    }

    VolumeListener listener;

    private FragmentDialogVolumeBinding binding;

    private Disposable volumeDisposable;
    private Disposable balanceDisposable;
    private Disposable balanceCenterDisposable;

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (VolumeListener) getParentFragment();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Calling fragment must implement Callback interface");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog);
        binding = FragmentDialogVolumeBinding.inflate(LayoutInflater.from(getContext()));
        builder.setView(binding.getRoot());
        return builder.create();
    }

    @Override
    public void initVolumeViews() {
        initSeekBarVolume();
        initImageButtonBalanceCenter();
    }

    private void initSeekBarVolume() {
        final int minSeekBarValue = 0;
        final int maxSeekBarValue = 100;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.seekBarVolume.setMin(minSeekBarValue);
        }
        binding.seekBarVolume.setMax(maxSeekBarValue);
        volumeDisposable = RxSeekBar.userChanges(binding.seekBarVolume)
                .skipInitialValue()
                .debounce(5, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(progress -> volumePresenter.seekBarVolumeProgressChanged(progress));
    }

    private void initImageButtonBalanceCenter() {
        balanceCenterDisposable = RxView.clicks(binding.imageButtonBalanceCenter)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(unit -> {
                    balancePresenter.seekBarBalanceProgressChanged(100);
                    binding.seekBarBalance.setProgress(100);
                });
    }

    @Override
    public void initBalanceViews() {
        initSeekBarBalance();
    }

    private void initSeekBarBalance() {
        final int minSeekBarValue = 0;
        final int maxSeekBarValue = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.seekBarBalance.setMin(minSeekBarValue);
        }
        binding.seekBarBalance.setMax(maxSeekBarValue);
        balancePresenter.initBalance();
        balanceDisposable = RxSeekBar.userChanges(binding.seekBarBalance)
                .skipInitialValue()
                .debounce(5, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(progress -> balancePresenter.seekBarBalanceProgressChanged(progress));
    }

    @Override
    public void setImageButtonVolumeSrc(int drawableId) {
    }

    @Override
    public void setTextViewVolumeText(String volume) {
        binding.textViewVolumeValue.setText(volume);
    }

    @Override
    public void setSeekBarVolumeProgress(int progress) {
        binding.seekBarVolume.setProgress(progress);
    }

    @Override
    public void setTextViewRightChannelValue(String value) {
        binding.textViewRightChannelValue.setText(value);
    }

    @Override
    public void setTextViewLeftChannelValue(String value) {
        binding.textViewLeftChannelValue.setText(value);
    }

    @Override
    public void setSeekBarBalanceProgress(int progress) {
        binding.seekBarBalance.setProgress(progress);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        volumeDisposable.dispose();
        balanceDisposable.dispose();
        balanceCenterDisposable.dispose();
        binding = null;
    }
}
