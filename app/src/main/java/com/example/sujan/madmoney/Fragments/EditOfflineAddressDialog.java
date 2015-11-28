package com.example.sujan.madmoney.Fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.sujan.madmoney.AppData.BTAddress;
import com.example.sujan.madmoney.AppData.GlobalStatic;
import com.example.sujan.madmoney.AppData.UserAddress;
import com.example.sujan.madmoney.R;
import com.example.sujan.madmoney.Resources.DBAddressBook;

import java.util.List;

/**
 * Created by Sujan on 11/28/2015.
 */
public class EditOfflineAddressDialog extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String deviceName = getArguments().getString("DEVICE_NAME");


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogue_add_address, null);
        setUserAddressValues(view, deviceName);

        builder.setView(view)
                .setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String name = ((EditText) getDialog().findViewById(R.id.ab_username)).getText() + "";
                        String addressId = ((EditText) getDialog().findViewById(R.id.ab_user_address_id)).getText() + "";
                        String phoneNo = ((EditText) getDialog().findViewById(R.id.ab_phone_number)).getText() + "";
                        int Id = Integer.parseInt((String) getDialog().findViewById(R.id.ab_username).getTag());
                        DBAddressBook dbAddressBook = new DBAddressBook(getActivity().getApplicationContext());
                        dbAddressBook.updateBTAddressTable(Id, name);
                    }
                })
                .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int Id = Integer.parseInt((String) getDialog().findViewById(R.id.ab_username).getTag());
                        DBAddressBook dbAddressBook = new DBAddressBook(getActivity().getApplicationContext());
                        dbAddressBook.deleteBTAddress(Id);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditOfflineAddressDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private void setUserAddressValues(View view, String deviceName) {
        DBAddressBook dbAddressBook = new DBAddressBook(getActivity().getApplicationContext());
        List<BTAddress> dbBTAddressBookList = dbAddressBook.selectBTAddress();
        for (BTAddress userAddress : dbBTAddressBookList) {
            if (userAddress.getDeviceName().equals(deviceName)) {
                ((EditText) view.findViewById(R.id.ab_username)).setText(userAddress.getDeviceName());
                ((EditText) view.findViewById(R.id.ab_username)).setTag(userAddress.getId());
                ((EditText) view.findViewById(R.id.ab_user_address_id)).setVisibility(View.INVISIBLE);
                ((EditText) view.findViewById(R.id.ab_phone_number)).setVisibility(View.INVISIBLE);
                break;
            }
        }
    }
}
