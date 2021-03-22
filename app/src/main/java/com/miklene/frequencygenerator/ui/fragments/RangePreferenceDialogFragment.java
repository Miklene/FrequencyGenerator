package com.miklene.frequencygenerator.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.arellomobile.mvp.MvpAppCompatDialogFragment;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.databinding.FragmentDialogRangePreferenceBinding;
import com.miklene.frequencygenerator.databinding.FragmentDialogVolumeBinding;

public class RangePreferenceDialogFragment extends MvpAppCompatDialogFragment {

    private FragmentDialogRangePreferenceBinding binding;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Dialog_Alert);
        binding = FragmentDialogRangePreferenceBinding.inflate(LayoutInflater.from(getContext()));
        builder.setView(binding.getRoot());
        builder.setMessage("Range");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }
}
