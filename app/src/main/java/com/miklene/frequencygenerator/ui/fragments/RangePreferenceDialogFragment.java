package com.miklene.frequencygenerator.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.arellomobile.mvp.MvpAppCompatDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.miklene.frequencygenerator.R;
import com.miklene.frequencygenerator.databinding.FragmentDialogRangePreferenceBinding;
import com.miklene.frequencygenerator.databinding.FragmentDialogVolumeBinding;

public class RangePreferenceDialogFragment extends MvpAppCompatDialogFragment {

    private FragmentDialogRangePreferenceBinding binding;
    private AlertDialog dialog;

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog);
        binding = FragmentDialogRangePreferenceBinding.inflate(LayoutInflater.from(getContext()));
        builder.setView(binding.getRoot());
        builder.setTitle("Range");
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
        builder.setNeutralButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    validateTextFields();
                    if (binding.textFieldFrom.getError() == null
                            && binding.textFieldTo.getError() == null)
                        dialog.dismiss();
                }
            });
        }
    }

    private void validateTextFields() {
        TextInputLayout fieldFrom = binding.textFieldFrom;
        TextInputLayout fieldTo = binding.textFieldTo;
        clearErrors(fieldFrom);
        clearErrors(fieldTo);
        int from=0;
        try {
            from = getValueFromTextField(fieldFrom);
            validateValue(from, fieldFrom);
        } catch (Exception e){
            showErrorEmptyField(fieldFrom);
        }
        int to=0;
        try {
            to = getValueFromTextField(fieldTo);
            validateValue(to, fieldTo);
        } catch (Exception e){
            showErrorEmptyField(fieldTo);
        }
        if(fieldFrom.getError()==null && fieldFrom.getError() == null){
            if(isFromGreaterTo(from,to)){
                fieldFrom.setError(getResources().getString(R.string.error_from_less_to));
                fieldTo.setError(getResources().getString(R.string.error_to_greater_from));
            }
        }

    }

    private void validateTextField(TextInputLayout field){
        int value;
        try {
            value = getValueFromTextField(field);
            validateValue(value, field);
        } catch (Exception e){
            showErrorEmptyField(field);
        }
    }

    private int getValueFromTextField(TextInputLayout field) throws Exception{
        try{
            TextInputEditText editText = (TextInputEditText) field.getEditText();
            return Integer.parseInt( editText.getText().toString());
        } catch (Exception e){
            throw new Exception();
        }
    }

    private void validateValue(int value, TextInputLayout field){
        if (value < 1) {
            showErrorLowerThan1(field);
        }
        if (value > 22000) {
            showErrorGreaterThan22000(field);
        }
    }

    private boolean isFromGreaterTo(int from, int to){
        return from > to;
    }




    private void validateTextFieldTo(){

    }


   /* private void checkFieldFrom() {
        binding.textFieldMinRange.setError(null);
        if (binding.textInputMinRange.getText().toString().equals("")) {
            binding.textFieldMinRange.setError(getResources().getString(R.string.error_empty_field));
        } else {
            int from = Integer.parseInt(binding.textInputMinRange.getText().toString());
            if (from < 1) {
                showErrorLowerThan1(binding.textFieldMinRange);
            }
            if (from > 22000) {
               showErrorGreaterThan22000(binding.textFieldMinRange);
            }

        }
    }



    private void checkFieldTo() {
        binding.textFieldMaxRange.setError(null);
        if (binding.textInputMaxRange.getText().toString().equals("")) {
            binding.textInputMaxRange.setError(getResources().getString(R.string.error_empty_field));
        } else {
            int to= Integer.parseInt(binding.textInputMaxRange.getText().toString());
            if (to < 1) {
                showErrorLowerThan1(binding.textFieldMaxRange);
            }
            if (to > 22000) {
                showErrorGreaterThan22000(binding.textFieldMaxRange);
            }
        }
    }
*/
    private void clearErrors(TextInputLayout field){
        field.setError(null);

    }

    private void showErrorGreaterThan22000(TextInputLayout field){
        field.setError(getResources().getString(R.string.error_greater_22000));
    }

    private void showErrorLowerThan1(TextInputLayout field){
        field.setError(getResources().getString(R.string.error_lower_one));
    }

    private void showErrorEmptyField(TextInputLayout field){
        field.setError(getResources().getString(R.string.error_empty_field));
    }
}
