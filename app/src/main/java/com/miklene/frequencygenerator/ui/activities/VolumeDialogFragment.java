package com.miklene.frequencygenerator.ui.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.arellomobile.mvp.MvpAppCompatDialogFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.miklene.frequencygenerator.mvp.presenters.BalancePresenter;
import com.miklene.frequencygenerator.mvp.presenters.VolumePresenter;
import com.miklene.frequencygenerator.mvp.views.BalanceView;
import com.miklene.frequencygenerator.mvp.views.VolumeView;
import com.miklene.frequencygenerator.databinding.FragmentDialogVolumeBinding;
import com.miklene.frequencygenerator.repository.PreferencesRepository;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;


public class VolumeDialogFragment extends MvpAppCompatDialogFragment implements VolumeView,
        BalanceView {

    private static final String PREFS_FILE = "Wave";

    @InjectPresenter
    VolumePresenter volumePresenter;

    @ProvidePresenter
    VolumePresenter provideVolumePresenter() {
        SharedPreferences preferences = Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        return new VolumePresenter(new PreferencesRepository(preferences));
    }

    @InjectPresenter
    BalancePresenter balancePresenter;

    @ProvidePresenter
    BalancePresenter provideBalancePresenter() {
        SharedPreferences preferences = Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        return new BalancePresenter(new PreferencesRepository(preferences));
    }

    public interface VolumeListener {
        void confirmButtonClicked();

        void cancelButtonClicked();
    }

    VolumeListener listener;

    private FragmentDialogVolumeBinding binding;

    private Observable<Long> volumeObservable;
    private DisposableObserver<Long> volumeDisposable;
    private Disposable balanceDisposable;

   /*@Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = binding.getRoot();
        initSeekBarVolume();
        initBalance();
        return view;
    }*/


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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        binding = FragmentDialogVolumeBinding.inflate(LayoutInflater.from(getContext()));
        builder.setView(binding.getRoot());
        return builder.create();
    }

    private void initSeekBarVolume() {
        final int minSeekBarValue = 0;
        final int maxSeekBarValue = 100;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.seekBarVolume.setMin(minSeekBarValue);
        }
        binding.seekBarVolume.setMax(maxSeekBarValue);
        volumePresenter.initVolume();
        binding.seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                volumeObservable = Observable.interval(50, TimeUnit.MILLISECONDS);
                volumeDisposable = new DisposableObserver<Long>() {
                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Long l) {
                        volumePresenter
                                .seekBarVolumeProgressChanged(binding.seekBarVolume.getProgress());
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                };
                volumeObservable.observeOn(AndroidSchedulers.mainThread())
                        .distinct()
                        .subscribe(volumeDisposable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (volumeDisposable != null)
                    volumeDisposable.dispose();
            }
        });
    }

    private void initBalance() {
        final int minSeekBarValue = 0;
        final int maxSeekBarValue = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.seekBarBalance.setMin(minSeekBarValue);
        }
        binding.seekBarBalance.setMax(maxSeekBarValue);
        balancePresenter.initBalance();
        binding.seekBarBalance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                balanceDisposable = Observable.interval(50, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(l -> balancePresenter.
                                seekBarBalanceProgressChanged(binding.seekBarBalance.getProgress()))
                        .distinct()
                        .subscribe();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (balanceDisposable != null)
                    balanceDisposable.dispose();
            }
        });
    }

    @Override
    public void initViews() {
        initSeekBarVolume();
        initBalance();
    }

    @Override
    public void setImageButtonVolumeSrc(int drawableId) {
    }

    @Override
    public void setTextViewVolumeValue(String volumeValue) {
        binding.textViewVolumeValue.setText(volumeValue);
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
        binding = null;
    }
}
