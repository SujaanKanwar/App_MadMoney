package com.payment.sujan.madmoney;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.payment.sujan.madmoney.R;

public class DepositInBankActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_in_bank);

        Toolbar toolbar = (Toolbar) findViewById(R.id.deposit_money_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

}
