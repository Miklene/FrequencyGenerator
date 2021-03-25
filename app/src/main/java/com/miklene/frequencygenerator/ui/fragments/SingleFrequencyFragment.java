package com.miklene.frequencygenerator.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Selection;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;


import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.jakewharton.rxbinding4.view.RxView;
import com.jakewharton.rxbinding4.widget.RxAdapterView;
import com.jakewharton.rxbinding4.widget.RxSeekBar;
import com.jakewharton.rxbinding4.widget.RxTextView;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.mvp.presenters.FrequencyPresenter;
import com.miklene.frequencygenerator.mvp.presenters.PlaybackPresenter;
import com.miklene.frequencygenerator.mvp.presenters.WaveTypePresenter;
import com.miklene.frequencygenerator.mvp.views.FrequencyView;
import com.miklene.frequencygenerator.mvp.views.PlaybackView;
import com.miklene.frequencygenerator.mvp.presenters.VolumePresenter;
import com.miklene.frequencygenerator.mvp.views.VolumeView;
import com.miklene.frequencygenerator.databinding.FragmentSingleFrequencyBinding;
import com.miklene.frequencygenerator.databinding.SpinnerRowBinding;
import com.miklene.frequencygenerator.mvp.views.WaveTypeView;
import com.miklene.frequencygenerator.repository.PreferencesRepository;
import com.miklene.frequencygenerator.repository.SettingsPreferencesRepository;
import com.miklene.frequencygenerator.repository.SettingsRepository;
import com.miklene.frequencygenerator.repository.WaveRepository;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;


public class SingleFrequencyFragment extends MvpAppCompatFragment implements PlaybackView,
        VolumeDialogFragment.VolumeListener, VolumeView, FrequencyView, WaveTypeView {
    public static final String ARG_PAGE = "ARG_PAGE";
    private SharedPreferences preferences;
    private WaveRepository waveRepository;
    private SettingsRepository settingsRepository;

    @InjectPresenter
    PlaybackPresenter playbackPresenter;

    @ProvidePresenter
    PlaybackPresenter providePlaybackPresenter() {
        return new PlaybackPresenter(getWaveRepository());
    }

    @InjectPresenter()
    VolumePresenter volumePresenter;

    @ProvidePresenter
    VolumePresenter provideVolumePresenter() {
        return new VolumePresenter(getWaveRepository());
    }

    @InjectPresenter
    FrequencyPresenter frequencyPresenter;

    @ProvidePresenter
    FrequencyPresenter provideFrequencyPresenter() {
        return new FrequencyPresenter(getWaveRepository(), getSettingsRepository());
    }

    @InjectPresenter
    WaveTypePresenter waveTypePresenter;

    @ProvidePresenter
    WaveTypePresenter provideWaveTypePresenter() {
        return new WaveTypePresenter(getWaveRepository());
    }

    private WaveRepository getWaveRepository() {
        if (waveRepository == null) {
            preferences = this.requireActivity()
                    .getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
            waveRepository = new PreferencesRepository(preferences);
        }
        return waveRepository;
    }

    private SettingsRepository getSettingsRepository() {
        if (settingsRepository == null) {
            settingsRepository = new SettingsPreferencesRepository(
                    PreferenceManager.getDefaultSharedPreferences(getContext()));
        }
        return settingsRepository;
    }

    private FragmentSingleFrequencyBinding binding;
    private SpinnerRowBinding spinnerRowBinding;
    String[] spinnerItems;
    final int[] images = new int[]{
            R.drawable.ic_sine,
            R.drawable.ic_sawtooth,
            R.drawable.ic_triangle,
            R.drawable.ic_square};

    private Disposable seekBarFrequencyDisposable;
    private Disposable editTextDisposable;
    private Disposable spinnerDisposable;
    private Disposable imageButtonPlayDisposable;

    private int cursorPosition;

    private static final String PREFS_FILE = "Wave";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSingleFrequencyBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        spinnerRowBinding = SpinnerRowBinding.inflate(getLayoutInflater());
        return view;
    }

    /**
     * VolumeView
     */
    @Override
    public void initVolumeViews() {
        initVolumeButton();
        initVolume();
    }

    public void initVolume() {
        volumePresenter.initVolume();
    }

    private void initVolumeButton() {
        binding.imageButtonVolume.setOnClickListener(v -> {
            v.startAnimation(new AlphaAnimation(1F, 0.8F));

            DialogFragment dialogFragment = new VolumeDialogFragment();
            dialogFragment.show(requireActivity()
                    .getSupportFragmentManager(), "TAG");
        });
    }

    @Override
    public void setImageButtonVolumeSrc(int drawableId) {
        binding.imageButtonVolume.setImageResource(drawableId);
    }

    @Override
    public void setTextViewVolumeText(String volume) {
        binding.textViewVolume.setText(volume);
    }

    @Override
    public void setSeekBarVolumeProgress(int progress) {
        //where is no SeekBar in this fragment
    }


    /**
     * FrequencyView
     */

    @Override
    public void initFrequencyViews() {
        initFrequencySeekBar();
        initEditTextFrequency();
        initDecreaseButton();
        initIncreaseButton();
    }

    private void initFrequencySeekBar() {
     /*   final int maxSeekBarValue = Integer.parseInt(settingsRepository.loadStringRangeTo()) -
                Integer.parseInt(settingsRepository.loadStringRangeFrom())+1;*/
       // binding.seekBarFrequency.setMax(maxSeekBarValue);
        seekBarFrequencyDisposable = RxSeekBar.userChanges(binding.seekBarFrequency)
                .skipInitialValue()
                .debounce(5, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(frequency -> frequencyPresenter.onSeekBarFrequencyChanged(frequency));
    }

    private void initEditTextFrequency() {
        editTextDisposable = RxTextView.textChanges(binding.editTextFrequency)
                .skipInitialValue()
                .debounce(250, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    cursorPosition = s.length() -
                            binding.editTextFrequency.getSelectionStart();
                    frequencyPresenter.onEditTextFrequencyTextChanges(s);
                });
        binding.editTextFrequency.setOnClickListener(v ->
                cursorPosition = binding.editTextFrequency.getText().toString().length() -
                        binding.editTextFrequency.getSelectionStart());
        binding.editTextFrequency.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER)
                hideKeyboard();
            return false;
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void initIncreaseButton() {
        RxView.touches(binding.imageButtonIncreaseFrequency)
                .debounce(5, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {
                    if (t.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        binding.imageButtonIncreaseFrequency.startAnimation(new AlphaAnimation(1F, 0.8F));
                        frequencyPresenter.onImageButtonIncreaseDown();
                    }
                    if (t.getActionMasked() == MotionEvent.ACTION_UP)
                        frequencyPresenter.onImageButtonIncreaseUp();
                    if (t.getActionMasked() == MotionEvent.ACTION_CANCEL)
                        frequencyPresenter.onImageButtonIncreaseUp();
                });
     /*   binding.imageButtonIncreaseFrequency.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                binding.imageButtonIncreaseFrequency.startAnimation(new AlphaAnimation(1F, 0.8F));
                frequencyPresenter.onImageButtonIncreaseDown();
            }
            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                binding.imageButtonIncreaseFrequency.performClick();
                frequencyPresenter.onImageButtonIncreaseUp();
            }
            return true;
        });*/
    }

    private void initDecreaseButton() {
        RxView.touches(binding.imageButtonDecreaseFrequency)
                .debounce(5, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(t -> {
                    if (t.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        binding.imageButtonDecreaseFrequency.startAnimation(new AlphaAnimation(1F, 0.8F));
                        frequencyPresenter.onImageButtonDecreaseDown();
                    }
                    if (t.getActionMasked() == MotionEvent.ACTION_UP)
                        frequencyPresenter.onImageButtonDecreaseUp();
                    if (t.getActionMasked() == MotionEvent.ACTION_CANCEL)
                        frequencyPresenter.onImageButtonDecreaseUp();
                });
      /*  binding.imageButtonDecreaseFrequency.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                binding.imageButtonDecreaseFrequency.startAnimation(new AlphaAnimation(1F, 0.8F));
                frequencyPresenter.onImageButtonDecreaseDown();
            }
            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                binding.imageButtonDecreaseFrequency.performClick();
                frequencyPresenter.onImageButtonDecreaseUp();
            }
            return true;
        });*/
    }


    @Override
    public void setEditTextFrequencyText(String frequency) {
        binding.editTextFrequency.setText(frequency);
        setEditTextSelection();
    }

    @Override
    public void setSeekBarFrequencyProgress(int progress) {
        binding.seekBarFrequency.setProgress(progress);
    }

    @Override
    public void setSeekBarMax(int progress) {
        binding.seekBarFrequency.setMax(progress);
    }

    private void setEditTextSelection() {
        int position = binding.editTextFrequency.getText().toString().length();
        Editable editable = binding.editTextFrequency.getText();
        int temp = position - cursorPosition;
        if (temp < 0)
            temp = 0;
        Selection.setSelection(editable, temp);
    }

    @Override
    public void vibrate() {
        Vibrator v = (Vibrator) requireActivity()
                .getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            v.vibrate(VibrationEffect
                    .createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        else
            v.vibrate(50);
    }

    /**
     * WaveTypeView
     */

    @Override
    public void initWaveTypeViews() {
        initSpinnerWaveType();
    }

    private void initSpinnerWaveType() {
        spinnerItems = requireActivity().getResources().getStringArray(R.array.wave_type);
        MyCustomAdapter adapter = new MyCustomAdapter(this.getActivity(), R.layout.spinner_row, spinnerItems);
        adapter.setDropDownViewResource(R.layout.spinner_row);
        binding.spinnerWaveType.setAdapter(adapter);
        spinnerDisposable = RxAdapterView.itemSelections(binding.spinnerWaveType)
                .skipInitialValue()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> waveTypePresenter.onSpinnerItemSelected(integer));
    }

    @Override
    public void setSpinnerWaveTypeItem(int position) {
        binding.spinnerWaveType.setSelection(position);
    }

    /**
     * PlaybackView
     */

    @Override
    public void initPlaybackViews() {
        initImageButtonPlay();
    }

    private void initImageButtonPlay() {
        imageButtonPlayDisposable = RxView.clicks(binding.imageButtonPlay)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit -> {
                    binding.imageButtonPlay.startAnimation(new AlphaAnimation(1F, 0.8F));
                    playbackPresenter.onImageButtonPlayClicked();
                });
    }

    @Override
    public void setImageButtonPlayBackground(int drawableId) {
        binding.imageButtonPlay.setImageResource(drawableId);
    }

    /**
     * Others
     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        seekBarFrequencyDisposable.dispose();
        editTextDisposable.dispose();
        spinnerDisposable.dispose();
        imageButtonPlayDisposable.dispose();
    }

    public static SingleFrequencyFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        SingleFrequencyFragment fragment = new SingleFrequencyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void confirmButtonClicked() {

    }

    @Override
    public void cancelButtonClicked() {

    }

    public class CustomImageButton extends AppCompatImageButton {

        public CustomImageButton(@NonNull Context context) {
            super(context);
        }

        public CustomImageButton(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    //frequencyPresenter.onImageButtonIncreaseDown();
                    break;
                case MotionEvent.ACTION_UP:
                    performClick();
                    break;
            }
            return true;
        }

        @Override
        public boolean performClick() {
            super.performClick();
            // frequencyPresenter.onImageButtonIncreaseUp();
            return true;
        }
    }


    class MyCustomAdapter extends ArrayAdapter<String> {

        public MyCustomAdapter(Context context, int textViewResourceId,
                               String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)
                        getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.spinner_row, parent, false);
            }
            ImageView imageView = convertView.findViewById(R.id.spinnerImageView);
            imageView.setImageResource(images[position]);
            return convertView;
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            String[] waveType = getResources().getStringArray(R.array.wave_type);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)
                        getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.spinner_row, parent, false);
            }
            TextView textView = convertView.findViewById(R.id.spinnerTextView);
            textView.setText(waveType[position]);
            ImageView imageView = convertView.findViewById(R.id.spinnerImageView);
            imageView.setImageResource(images[position]);
            return convertView;
        }
    }
}
