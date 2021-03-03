package com.miklene.frequencygenerator.ui.activities;

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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;


import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.jakewharton.rxbinding4.widget.RxSeekBar;
import com.jakewharton.rxbinding4.widget.RxTextView;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.mvp.presenters.FrequencyPresenter;
import com.miklene.frequencygenerator.mvp.presenters.SingleFrequencyPresenter;
import com.miklene.frequencygenerator.mvp.presenters.SharedPrefPresenter;
import com.miklene.frequencygenerator.mvp.views.FrequencyView;
import com.miklene.frequencygenerator.mvp.views.SingleFrequencyView;
import com.miklene.frequencygenerator.mvp.views.SharedPrefView;
import com.miklene.frequencygenerator.mvp.presenters.VolumePresenter;
import com.miklene.frequencygenerator.mvp.views.VolumeView;
import com.miklene.frequencygenerator.databinding.FragmentSingleFrequencyBinding;
import com.miklene.frequencygenerator.databinding.SpinnerRowBinding;
import com.miklene.frequencygenerator.repository.PreferencesRepository;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

import static android.widget.Toast.makeText;


public class SingleFrequencyFragment extends MvpAppCompatFragment implements SingleFrequencyView,
        VolumeDialogFragment.VolumeListener, /*SharedPrefView,*/ VolumeView, FrequencyView {
    public static final String ARG_PAGE = "ARG_PAGE";

    @InjectPresenter
    SingleFrequencyPresenter singleFrequencyPresenter;

    @InjectPresenter()
    VolumePresenter volumePresenter;

    @ProvidePresenter
    VolumePresenter provideVolumePresenter(){
        preferences = Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        return new VolumePresenter(new PreferencesRepository(preferences));
    }

   /* @InjectPresenter
    SharedPrefPresenter sharedPrefPresenter;

    @ProvidePresenter
    SharedPrefPresenter provideSharedPrefRepository() {
        preferences = Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        return new SharedPrefPresenter(new PreferencesRepository(preferences));
    }*/

    @InjectPresenter
    FrequencyPresenter frequencyPresenter;

    @ProvidePresenter
    FrequencyPresenter provideFrequencyPresenter(){
        preferences = Objects.requireNonNull(this.getActivity())
                .getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        return new FrequencyPresenter(new PreferencesRepository(preferences));
    }

    private FragmentSingleFrequencyBinding binding;
    private SpinnerRowBinding spinnerRowBinding;
    String[] spinnerItems;
    int[] images = new int[]{
            R.drawable.ic_sine,
            R.drawable.ic_sawtooth,
            R.drawable.ic_triangle,
            R.drawable.ic_square};

    private long lastAction = 0L;
    private DisposableObserver<Long> disposable;
    private DisposableObserver<String> editTextDisposable;
    private Disposable seekBarFrequencyDisposable;
    private Observable<Long> observable;
    private int cursorPosition;

    private static final String PREFS_FILE = "Wave";
    private static final String PREFS_FREQUENCY = "Frequency";
    private static final String PREFS_WAVE_TYPE = "WaveType";
    private static final String PREFS_VOLUME = "Volume";
    private SharedPreferences preferences;
    private SharedPreferences.Editor prefEditor;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSingleFrequencyBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        spinnerRowBinding = SpinnerRowBinding.inflate(getLayoutInflater());
        initSpinnerWaveType();
        binding.imageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.8F));
                singleFrequencyPresenter.onImageButtonPlayClicked();
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        //Presenter
       /* String type = binding.spinnerWaveType.getSelectedItem().toString().toUpperCase();
        float frequency = Float.parseFloat(binding.editTextFrequency.getText().toString());
        int volume = binding.seekBarVolume.getProgress();
        sharedPrefPresenter.save(type, frequency, volume);*/
        /*prefEditor = preferences.edit();
        prefEditor.putFloat(PREFS_FREQUENCY, frequency);
        prefEditor.putString(PREFS_WAVE_TYPE,type);
        prefEditor.putInt(PREFS_VOLUME, volume);
        prefEditor.apply();*/
    }

 /*   @Override
    public void setEditTextValue(String value) {
        binding.editTextFrequency.setText(value);
        setEditTextSelection();
    }*/

    private void initSpinnerWaveType() {
        spinnerItems = Objects.requireNonNull(getActivity()).getResources().getStringArray(R.array.wave_type);
        MyCustomAdapter adapter = new MyCustomAdapter(this.getActivity(), R.layout.spinner_row, spinnerItems);
        adapter.setDropDownViewResource(R.layout.spinner_row);
        binding.spinnerWaveType.setAdapter(adapter);
        //Presenter
        /*String type = preferences.getString(PREFS_WAVE_TYPE, "SINE");
        for(int i = 0;i<spinnerItems.length;i++)
            if(spinnerItems[i].toUpperCase().equals(type)) {
                binding.spinnerWaveType.setSelection(i);
                break;
            }*/
    }



    @Override
    public void disposeEditTextObservable() {
        if (editTextDisposable != null) {
            editTextDisposable.dispose();
        }
    }





    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }



    /**VolumeView
     *
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
        binding.imageButtonVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.8F));

                DialogFragment dialogFragment = new VolumeDialogFragment();
                dialogFragment.show(Objects.requireNonNull(getActivity())
                        .getSupportFragmentManager(), "TAG");
            }
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



    /**FrequencyView
     *
     */

    @Override
    public void initFrequencyViews() {
        initFrequencySeekBar();
        initEditTextFrequency();
        initDecreaseButton();
        initIncreaseButton();
    }

    private void initFrequencySeekBar() {
        final int maxSeekBarValue = 14425215;
        final int minSeekBarValue = 0;
        binding.seekBarFrequency.setMax(maxSeekBarValue);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.seekBarFrequency.setMin(minSeekBarValue);
        }
        seekBarFrequencyDisposable =  RxSeekBar.userChanges(binding.seekBarFrequency)
                .skipInitialValue()
                .debounce(5, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(frequency -> frequencyPresenter.onSeekBarFrequencyChanged(frequency));
    }

    private void initEditTextFrequency() {
        binding.editTextFrequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursorPosition = binding.editTextFrequency.getText().toString().length() -
                        binding.editTextFrequency.getSelectionStart();
            }
        });
        binding.editTextFrequency.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    subscribeEditTextObservable();
                    hideKeyboard();
                }
                return false;
            }
        });
    }

    @Override
    public void subscribeEditTextObservable() {
        Observable<String> observable = RxTextView.textChanges(binding.editTextFrequency)
                .debounce(250, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnDispose(() -> {
                    Toast toast = makeText(getActivity(), "Disposed", Toast.LENGTH_LONG);
                    toast.show();
                });
        editTextDisposable = new DisposableObserver<String>() {
            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull String s) {
                cursorPosition = binding.editTextFrequency.getText().toString().length() -
                        binding.editTextFrequency.getSelectionStart();
                singleFrequencyPresenter.onEditTextTextChanges(s);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        observable.subscribe(editTextDisposable);
    }

    private void initIncreaseButton() {
        binding.imageButtonIncreaseFrequency.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
                    singleFrequencyPresenter.onImageButtonIncreaseDown();
                if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    binding.imageButtonIncreaseFrequency.performClick();
                    singleFrequencyPresenter.onImageButtonIncreaseUp();
                }
                return true;
            }
        });
    }

    private void initDecreaseButton() {
        binding.imageButtonDecreaseFrequency.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
                    singleFrequencyPresenter.onImageButtonDecreaseDown();
                if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    binding.imageButtonDecreaseFrequency.performClick();
                    singleFrequencyPresenter.onImageButtonDecreaseUp();
                }
                return true;
            }
        });
    }

    @Override
    public void setEditTextFrequencyText(String frequency) {
        binding.editTextFrequency.setText(frequency);
        setEditTextSelection();
    }

    private void setEditTextSelection() {
        int position = binding.editTextFrequency.getText().toString().length();
        Editable editable = binding.editTextFrequency.getText();
        int temp = position - cursorPosition;
        if (temp < 0)
            temp = 0;
        Selection.setSelection(editable, temp);
    }

    public void setSeekBarProgress(int progress) {
        binding.seekBarFrequency.setProgress(progress);
    }

    private Observable<Integer> seekBarObservable() {
        return RxSeekBar.changes(binding.seekBarFrequency)
                .debounce(10, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread());
        // .subscribe(integer -> {
        //     mainPresenter.seekBarProgressChanged(integer);
        // });
    }

    private int calculateSeekBarProgress(double value) {
        return (int) ((Math.log(value) / Math.log(2)) * 1000000);
    }


    @Override
    public void vibrate() {
        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE); // Vibrate for 400 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        else
            v.vibrate(50);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        seekBarFrequencyDisposable.dispose();
    }

    public static SingleFrequencyFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        SingleFrequencyFragment fragment = new SingleFrequencyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setImageButtonPlayBackground(int drawableId) {
        binding.imageButtonPlay.setImageResource(drawableId);
    }



    @Override
    public void confirmButtonClicked() {

    }

    @Override
    public void cancelButtonClicked() {

    }

 /*   @Override
    public void setSeekBarFrequencyProgress(int progress) {
        binding.seekBarFrequency.setProgress(progress);
    }

    @Override
    public void setSpinnerWaveType() {

    }

    @Override
    public void setTextViewVolumeValue(String value) {

    }*/

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
                    singleFrequencyPresenter.onImageButtonIncreaseDown();
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
            singleFrequencyPresenter.onImageButtonIncreaseUp();
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
