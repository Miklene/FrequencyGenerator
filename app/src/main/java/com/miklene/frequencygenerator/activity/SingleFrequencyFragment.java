package com.miklene.frequencygenerator.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.miklene.frequencygenerator.R;

public class SingleFrequencyFragment extends MvpAppCompatFragment implements PlaybackView {
    public static final String ARG_PAGE = "ARG_PAGE";

    @InjectPresenter
    MainPresenter mainPresenter;

    private ImageButton imageButtonPlay;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_frequency, container, false);
        imageButtonPlay = view.findViewById(R.id.imageButtonPlaySingleFrequency);
        imageButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainPresenter.onImageButtonPlayClicked();
            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        imageButtonPlay.setBackground(ContextCompat.getDrawable(getActivity(), drawableId));
    }
}
