package com.miklene.frequencygenerator.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.jakewharton.rxbinding4.widget.RxTextView;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.databinding.FragmentSingleFrequencyBinding;
import com.miklene.frequencygenerator.databinding.SpinnerRowBinding;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class SingleFrequencyFragment extends MvpAppCompatFragment implements PlaybackView,
        SeekBar.OnSeekBarChangeListener {
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
    private int userSeekBarValue = 200;
    private static final long REPEAT_INTERVAL = 150L; // интервал повтора в миллисекундах
    private long lastAction = 0L;
    private DisposableObserver disposable;
    private int repeats = 0;

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
        binding.imageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(new AlphaAnimation(1F, 0.8F));
                mainPresenter.onImageButtonPlayClicked();
            }
        });
        return view;
    }

    private void initEditTextFrequency() {
        binding.editTextFrequency.setText(String.valueOf(userSeekBarValue));
        int position = String.valueOf(userSeekBarValue).length();
        Editable editable =  binding.editTextFrequency.getText();
        Selection.setSelection(editable, position);
        RxTextView.textChanges(binding.editTextFrequency)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                    binding.seekBarFrequency.setProgress(
                            calculateSeekBarProgress(Double.parseDouble(String.valueOf(charSequence))));
                });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void initSeekBar() {
        binding.seekBarFrequency.setMax(maxSeekBarValue);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.seekBarFrequency.setMin(minSeekBarValue);
        }
        binding.seekBarFrequency.setProgress(calculateSeekBarProgress(userSeekBarValue));
        binding.editTextFrequency.setText(String.valueOf(userSeekBarValue));
        binding.seekBarFrequency.setOnSeekBarChangeListener(this);

    }

    private int calculateSeekBarProgress(double value) {
        return (int) ((Math.log(value) / Math.log(2)) * 1000000);
    }

    public void setSeekBarProgress(int progress){
        binding.seekBarFrequency.setProgress(progress);
    }

    private void initIncreaseButton() {
        binding.imageButtonIncreaseFrequency.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                long currTime = SystemClock.uptimeMillis();
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN: {
                        repeats = 0;
                        Observable<Long> observable = Observable.interval(500, 100, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread());
                        disposable = new DisposableObserver() {
                            @Override
                            public void onNext(@io.reactivex.annotations.NonNull Object o) {
                                int currentFrequency = Integer.valueOf(binding.editTextFrequency.getText().toString());
                                repeats++;
                                if (repeats <= 10)
                                    currentFrequency++;
                                if (repeats > 10)
                                    currentFrequency += 10;
                                Log.d("TAG", "onNext: " + currentFrequency);
                                binding.editTextFrequency.setText(String.valueOf(currentFrequency));
                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                Log.d("TAG", "onError: " + e);
                            }

                            @Override
                            public void onComplete() {
                                Log.d("TAG", "onComplete: ");
                            }
                        };
                        observable.observeOn(AndroidSchedulers.mainThread());
                        observable.subscribe(disposable);


                        /*Observable.interval(500, TimeUnit.MILLISECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new io.reactivex.Observer<Long>() {
                                    @Override
                                    public void onSubscribe(@io.reactivex.annotations.NonNull io.reactivex.disposables.Disposable d) {
                                        disposable = d;
                                    }

                                    @Override
                                    public void onNext(@io.reactivex.annotations.NonNull Long aLong) {
                                        int currentFrequency = Integer.valueOf(binding.editTextFrequency.getText().toString());
                                        binding.editTextFrequency.setText(String.valueOf(currentFrequency++));
                                    }

                                    @Override
                                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                        Log.d("TAG", "onError: " + e);
                                    }

                                    @Override
                                    public void onComplete() {
                                        Log.d("TAG", "onComplete: ");
                                    }
                                });*/
                    /*    lastAction = currTime;

                        if (userSeekBarValue + 1 < 22000) {
                            userSeekBarValue += 1;
                            int progress = (int) ((Math.log(userSeekBarValue) / Math.log(2)) * 1000000);
                            binding.seekBarFrequency.setProgress((int) progress);
                            break;
                        }
                        binding.seekBarFrequency.setProgress(maxSeekBarValue);*/
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        if (repeats == 0) {
                            int currentFrequency = Integer.valueOf(binding.editTextFrequency.getText().toString());
                            binding.editTextFrequency.setText(String.valueOf(++currentFrequency));
                        }
                        disposable.dispose();
                        break;
                    }
                }
                return true;
            }
        });

        /*binding.imageButtonIncreaseFrequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mainPresenter.onImageButtonIncreaseClicked();
                if (userSeekBarValue + 1 < 22000) {
                    userSeekBarValue += 1;
                    int progress = (int) ((Math.log(userSeekBarValue) / Math.log(2)) * 1000000);
                    binding.seekBarFrequency.setProgress((int) progress);
                    return;
                }
                binding.seekBarFrequency.setProgress(maxSeekBarValue);
            }
        });
        binding.imageButtonIncreaseFrequency.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (userSeekBarValue + 1 < 22000) {
                    userSeekBarValue += 1;
                    int progress = (int) ((Math.log(userSeekBarValue) / Math.log(2)) * 1000000);
                    binding.seekBarFrequency.setProgress((int) progress);
                    try {
                        Thread.sleep(100);
                    } catch (Exception ex) {

                    }
                }
                return true;
            }
        });*/
    }


    private void initDecreaseButton() {
        binding.imageButtonDecreaseFrequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mainPresenter.onImageButtonDecreaseClicked();
                if (userSeekBarValue - 1 > 0) {
                    userSeekBarValue -= 1;
                    int progress = (int) ((Math.log(userSeekBarValue) / Math.log(2)) * 1000000);
                    binding.seekBarFrequency.setProgress((int) progress);
                    return;
                }
                binding.seekBarFrequency.setProgress(minSeekBarValue);
            }
        });
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

    @Override
    public void setEditTextValue(int value) {
        binding.editTextFrequency.setText(String.valueOf(value));
        int position = String.valueOf(userSeekBarValue).length();
        Editable editable =  binding.editTextFrequency.getText();
        Selection.setSelection(editable, position);
    }

    //SeekBar
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        userSeekBarValue = (int) Math.ceil(Math.pow(2, (progress / 1000000d)));
        setEditTextValue(userSeekBarValue);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    //@Override
    public int getUserSeekBarValue() {
        return userSeekBarValue;
    }

   /* @Override
    public void setUserSeekBarValue(int userSeekBarValue) {
        this.userSeekBarValue = userSeekBarValue;
    }*/

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
