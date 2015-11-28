package com.payment.sujan.madmoney.Fragments;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.payment.sujan.madmoney.Fragments.AddNewAddressDialog;
import com.payment.sujan.madmoney.MainActivity;
import com.payment.sujan.madmoney.R;

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
        com.payment.sujan.madmoney.Fragments.AddNewAddressDialog addNewAddressDialog = new com.payment.sujan.madmoney.Fragments.AddNewAddressDialog();
        addNewAddressDialog.show(fragmentManager,"ADD_NEW_ADDRESS");
    }

}
