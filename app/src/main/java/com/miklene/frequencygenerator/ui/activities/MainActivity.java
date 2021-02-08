package com.miklene.frequencygenerator.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;

import android.os.Bundle;
import android.view.View;


import com.miklene.frequencygenerator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        FragmentPagerAdapter adapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        binding.viewPager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}