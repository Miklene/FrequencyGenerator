package com.miklene.frequencygenerator.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;


import com.google.android.material.tabs.TabLayout;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.viewPager.setAdapter(
                new MainFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this));
        binding.tabLayout.setupWithViewPager( binding.viewPager);
    }

}