package com.example.sanctuary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class EmergencyContacts extends AppCompatDialogFragment {
    private int EC_which;

    private EditText editTextName;
    private EditText editTextNumber;
    private EmergencyContactsDialogListener ecdListener;

    public EmergencyContacts(int which)
    {
        this.EC_which = which;
    }






    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if(EC_which == 1 || EC_which == 2 || EC_which == 3)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.ecinput, null);
            builder.setView(view)

                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = editTextName.getText().toString();
                            String number = editTextNumber.getText().toString();
                            ecdListener.applyText(name, number, EC_which);
                        }
                    });

            editTextName  = view.findViewById(R.id.edit_contactName);
            editTextNumber  = view.findViewById(R.id.edit_contactNumber);
            return builder.create();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.ecinput, null);
            builder.setView(view)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String name = editTextName.getText().toString();
                            String number = editTextNumber.getText().toString();
                            ecdListener.applyText(name, number, EC_which);
                        }
                    });

            editTextName  = view.findViewById(R.id.edit_contactName);
            editTextNumber  = view.findViewById(R.id.edit_contactNumber);
            return builder.create();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            ecdListener = (EmergencyContactsDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "have not implement EmergencyContactsDialogListener");
        }
    }

    public interface EmergencyContactsDialogListener{
        void applyText(String ecName, String ecNumber, int which);
    }
}
