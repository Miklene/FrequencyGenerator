package com.miklene.frequencygenerator.ui.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.repository.SettingsPreferencesRepository;
import com.miklene.frequencygenerator.repository.SettingsRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Disposable rangeDisposable;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_settings, rootKey);
            /*EditTextPreference rangeFromPreference = findPreference("range_from");
            if (rangeFromPreference != null) {
                rangeFromPreference.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                    @Override
                    public void onBindEditText(@NonNull EditText editText) {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                });
            }
            EditTextPreference rangeToPreference = findPreference("range_to");
            if (rangeToPreference != null) {
                rangeToPreference.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                    @Override
                    public void onBindEditText(@NonNull EditText editText) {
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                });
            }*/
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals("range")) {
            DialogFragment rangeDialog = new RangePreferenceDialogFragment();
            rangeDialog.show(requireActivity()
                    .getSupportFragmentManager(), "RANGE");
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preference rangePreference = findPreference("range");
        SettingsRepository repository = new SettingsPreferencesRepository(
                PreferenceManager.getDefaultSharedPreferences(requireActivity()));
        if (rangePreference != null) {
            rangeDisposable = repository.getRangeSubject()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(rangePreference::setSummary);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rangeDisposable != null)
            rangeDisposable.dispose();
    }
}

