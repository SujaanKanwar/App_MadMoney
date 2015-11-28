package com.example.sujan.madmoney.Fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.sujan.madmoney.AppData.GlobalStatic;
import com.example.sujan.madmoney.AppData.UserAddress;
import com.example.sujan.madmoney.R;
import com.example.sujan.madmoney.Resources.DBAddressBook;

import java.util.List;

/**
 * Created by Sujan on 11/27/2015.
 */
public class EditOnlineAddressDialog extends DialogFragment {
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int addressId = getArguments().getInt("addressId");


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogue_add_address, null);
        setUserAddressValues(view, addressId);

        builder.setView(view)
                .setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String name = ((EditText) getDialog().findViewById(R.id.ab_username)).getText() + "";
                        String addressId = ((EditText) getDialog().findViewById(R.id.ab_user_address_id)).getText() + "";
                        String phoneNo = ((EditText) getDialog().findViewById(R.id.ab_phone_number)).getText() + "";
                        int Id = Integer.parseInt((String) getDialog().findViewById(R.id.ab_username).getTag());
                        DBAddressBook dbAddressBook = new DBAddressBook(getActivity().getApplicationContext());
                        dbAddressBook.updateUserAddressTable(Id, name, addressId, phoneNo);
                    }
                })
                .setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int Id = Integer.parseInt((String) getDialog().findViewById(R.id.ab_username).getTag());
                        DBAddressBook dbAddressBook = new DBAddressBook(getActivity().getApplicationContext());
                        dbAddressBook.deleteUserAddress(Id);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditOnlineAddressDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private void setUserAddressValues(View view, int addressId) {
        List<UserAddress> addressList = GlobalStatic.getOnlineUserAddressList();
        for (UserAddress userAddress : addressList) {
            if (userAddress.getId().equals(addressId +"")) {
                ((EditText) view.findViewById(R.id.ab_username)).setText(userAddress.getUserName());
                ((EditText) view.findViewById(R.id.ab_username)).setTag(userAddress.getId());
                ((EditText) view.findViewById(R.id.ab_user_address_id)).setText(userAddress.getUserAddressId());
                ((EditText) view.findViewById(R.id.ab_phone_number)).setText(userAddress.getPhoneNo());
                break;
            }
        }
    }
}
