package com.example.sujan.madmoney.Fragments;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.example.sujan.madmoney.MainActivity;
import com.example.sujan.madmoney.R;

/**
 * Created by Sujan on 11/26/2015.
 */
public class onAddNewAddressListener implements View.OnClickListener {

    Context context;
    FragmentManager fragmentManager;

    public onAddNewAddressListener(Context applicationContext, FragmentManager fragmentManager) {
        context = applicationContext;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onClick(View v) {
        AddNewAddressDialog addNewAddressDialog = new AddNewAddressDialog();
        addNewAddressDialog.show(fragmentManager,"ADD_NEW_ADDRESS");
    }

}
