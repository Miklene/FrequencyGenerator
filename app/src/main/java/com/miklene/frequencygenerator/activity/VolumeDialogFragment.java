package com.miklene.frequencygenerator.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.activity.volume.VolumePresenter;
import com.miklene.frequencygenerator.databinding.FragmentDialogVolumeBinding;
import com.miklene.frequencygenerator.databinding.FragmentSingleFrequencyBinding;

import org.jetbrains.annotations.NotNull;


public class VolumeDialogFragment extends DialogFragment {

    @InjectPresenter
    VolumePresenter volumePresenter;

    public interface VolumeListener{
        void confirmButtonClicked();
        void cancelButtonClicked();
    }

    VolumeListener listener;

    private FragmentDialogVolumeBinding binding;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentDialogVolumeBinding.inflate(inflater,container,false);
        View view  = binding.getRoot();
        binding.seekBarVolume.setMax(100);

        return view;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (VolumeListener) getParentFragment();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Calling fragment must implement Callback interface");
        }
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.fragment_dialog_volume, null));
        return builder.create();
    }
}
