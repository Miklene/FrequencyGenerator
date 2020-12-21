package com.miklene.frequencygenerator.activity;

import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Selection;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageButton;


import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.jakewharton.rxbinding4.widget.RxSeekBar;
import com.jakewharton.rxbinding4.widget.RxTextView;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.databinding.FragmentSingleFrequencyBinding;
import com.miklene.frequencygenerator.databinding.SpinnerRowBinding;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static android.os.VibrationEffect.EFFECT_CLICK;
import static android.widget.Toast.makeText;


public class SingleFrequencyFragment extends MvpAppCompatFragment implements PlaybackView {
    public static final String ARG_PAGE = "ARG_PAGE";

    @InjectPresenter
    MainPresenter mainPresenter;

    private FragmentSingleFrequencyBinding binding;
    private SpinnerRowBinding spinnerRowBinding;
    String[] array;
    int[] images = new int[]{
            R.drawable.ic_sine,
            R.drawable.ic_sawtooth,
            R.drawable.ic_triangle,
            R.drawable.ic_square};
    private final int maxSeekBarValue = 14425215;
    private final int minSeekBarValue = 0;
    private static final long REPEAT_INTERVAL = 150L; // интервал повтора в миллисекундах
    private long lastAction = 0L;
    private DisposableObserver<Long> disposable;
    private DisposableObserver<String> editTextDisposable;
    private DisposableObserver<Long> seekBarDisposable;
    private Disposable seekBarVolumeDisposable;
    private Observable<Long> observable;
    private int repeats = 0;
    private int cursorPosition;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSingleFrequencyBinding.inflate(inflater, container, false);
        array = getActivity().getResources().getStringArray(R.array.wave_type);
        spinnerRowBinding = SpinnerRowBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        MyCustomAdapter adapter = new MyCustomAdapter(this.getActivity(), R.layout.spinner_row, array);
        adapter.setDropDownViewResource(R.layout.spinner_row);
        binding.spinnerWaveType.setAdapter(adapter);
        initSeekBar();
        initIncreaseButton();
        initDecreaseButton();
        initEditTextFrequency();
        initVolumeButton();
        initSeekBarVolume();
        binding.imageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.8F));
                mainPresenter.onImageButtonPlayClicked();
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setEditTextValue(String value) {
        binding.editTextFrequency.setText(value);
        setEditTextSelection();
    }

    private void initEditTextFrequency() {
        binding.editTextFrequency.setText(String.valueOf(200));
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
                mainPresenter.onEditTextTextChanges(s);
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

    @Override
    public void disposeEditTextObservable() {
        if (editTextDisposable != null) {
            editTextDisposable.dispose();
        }
    }

    @Override
    public void setTextViewVolumeValue(String volumeValue) {
        binding.textViewVolume.setText(volumeValue);
    }

    @Override
    public void setImageButtonVolumeSrc(int drawableId) {
        binding.imageButtonVolume.setImageResource(drawableId);
    }

    private void setEditTextSelection() {
        int position = binding.editTextFrequency.getText().toString().length();
        Editable editable = binding.editTextFrequency.getText();
        int temp = position - cursorPosition;
        if (temp < 0)
            temp = 0;
        Selection.setSelection(editable, temp);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void initSeekBar() {
        binding.seekBarFrequency.setMax(maxSeekBarValue);
        binding.seekBarFrequency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                observable = Observable.interval(25, TimeUnit.MILLISECONDS);
                seekBarDisposable = new DisposableObserver<Long>() {
                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Long l) {
                        mainPresenter.seekBarProgressChanged(binding.seekBarFrequency.getProgress());
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                };
                observable.observeOn(AndroidSchedulers.mainThread())
                        .distinct()
                        .subscribe(seekBarDisposable);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast toast = Toast.makeText(getActivity(), "Dispose", Toast.LENGTH_SHORT);
                toast.show();
                if (seekBarDisposable != null)
                    seekBarDisposable.dispose();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.seekBarFrequency.setMin(minSeekBarValue);
        }
        binding.seekBarFrequency.setProgress(calculateSeekBarProgress(200));
        binding.editTextFrequency.setText(String.valueOf(200));
    }

    public void initSeekBarVolume() {
        binding.seekBarVolume.setMax(100);
        mainPresenter.initVolumeElements();
    }

    private Observable<Integer> seekBarVolumeObserve(){
        return  RxSeekBar.changes(binding.seekBarVolume)
                .debounce(10,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public void setSeekBarVolumeProgress(int progress) {
        binding.seekBarVolume.setProgress(progress);
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

    private void initVolumeButton() {
        binding.imageButtonVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.8F));
                if (binding.seekBarVolume.getVisibility() == View.GONE) {
                    TransitionManager.beginDelayedTransition(binding.layout, new AutoTransition());
                    binding.seekBarVolume.setVisibility(View.VISIBLE);
                    Observable<Integer> observable =  seekBarVolumeObserve();
                    seekBarVolumeDisposable = observable
                            .subscribe(integer -> mainPresenter.seekBarVolumeProgressChanged(integer));
                } else {
                    TransitionManager.beginDelayedTransition(binding.layout, new AutoTransition());
                    binding.seekBarVolume.setVisibility(View.GONE);
                    seekBarVolumeDisposable.dispose();
                }
            }
        });
    }

    private void initIncreaseButton() {
        binding.imageButtonIncreaseFrequency.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
                    mainPresenter.onImageButtonIncreaseDown();
                if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    binding.imageButtonIncreaseFrequency.performClick();
                    mainPresenter.onImageButtonIncreaseUp();
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
                    mainPresenter.onImageButtonDecreaseDown();
                if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    binding.imageButtonDecreaseFrequency.performClick();
                    mainPresenter.onImageButtonDecreaseUp();
                }
                return true;
            }
        });
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
                    mainPresenter.onImageButtonIncreaseDown();
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
            mainPresenter.onImageButtonIncreaseUp();
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
