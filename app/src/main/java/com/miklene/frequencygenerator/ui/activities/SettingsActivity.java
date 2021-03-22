package com.miklene.frequencygenerator.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.databinding.ActivityMainBinding;
import com.miklene.frequencygenerator.databinding.ActivitySettingsBinding;
import com.miklene.frequencygenerator.ui.fragments.RangePreferenceDialogFragment;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.toolbar.setTitle(getString(R.string.settings));
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this);
        String lanSettings = prefs.getString("Scale", null);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_settings, rootKey);
            EditTextPreference rangeFromPreference = findPreference("range_from");
            if(rangeFromPreference!=null){
                rangeFromPreference.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                    @Override
                    public void onBindEditText(@NonNull EditText editText) {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                });
            }
            EditTextPreference rangeToPreference = findPreference("range_to");
            if(rangeToPreference!=null){
                rangeToPreference.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                    @Override
                    public void onBindEditText(@NonNull EditText editText) {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                });
            }
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            if(preference.getKey().equals("Range")){
                DialogFragment rangeDialog = new RangePreferenceDialogFragment();
                rangeDialog.show(requireActivity()
                        .getSupportFragmentManager(), "RANGE");

            }
            return super.onPreferenceTreeClick(preference);
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }
    }
}
