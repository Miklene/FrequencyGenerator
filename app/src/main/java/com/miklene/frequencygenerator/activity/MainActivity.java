package com.miklene.frequencygenerator.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.os.Bundle;


import com.google.android.material.tabs.TabLayout;
import com.miklene.frequencygenerator.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Получаем ViewPager и устанавливаем в него адаптер
        ViewPager viewPager = findViewById(R.id.viewPagerMain);
        viewPager.setAdapter(
                new MainFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this));
        // Передаём ViewPager в TabLayout
        TabLayout tabLayout = findViewById(R.id.tabLayoutMain);
        tabLayout.setupWithViewPager(viewPager);
    }

}