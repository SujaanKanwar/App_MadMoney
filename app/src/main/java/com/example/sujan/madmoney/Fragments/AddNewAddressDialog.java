package com.example.sujan.madmoney.Fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.example.sujan.madmoney.R;
import com.example.sujan.madmoney.Resources.DBAddressBook;

/**
 * Created by Sujan on 11/26/2015.
 */
public class AddNewAddressDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialogue_add_address, null))

                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String name = ((EditText) getDialog().findViewById(R.id.ab_username)).getText() + "";
                        String addressId = ((EditText) getDialog().findViewById(R.id.ab_user_address_id)).getText() + "";
                        String phoneNo = ((EditText) getDialog().findViewById(R.id.ab_phone_number)).getText() + "";

                        DBAddressBook dbAddressBook = new DBAddressBook(getActivity().getApplicationContext());
                        dbAddressBook.insertUserAddress(name, addressId, phoneNo);
                    }
                })

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddNewAddressDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
