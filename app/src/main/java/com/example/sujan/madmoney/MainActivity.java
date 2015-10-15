package com.example.sujan.madmoney;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.View;

import com.example.sujan.madmoney.AppData.GlobalStatic;
import com.example.sujan.madmoney.Fragments.BucketFragment;
import com.example.sujan.madmoney.Fragments.ReceiverFragment;
import com.example.sujan.madmoney.Fragments.UserCreateFragment;
import com.example.sujan.madmoney.Fragments.WalletFragment;


public class MainActivity extends Activity implements UserCreateFragment.XMLClickables, WalletFragment.XMLClickables {

    private UserCreateFragment userCreateFragment;
    private WalletFragment walletFragment;
    private ReceiverFragment receiverFragment;
    private BucketFragment bucketFragment;

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

            SharedPreferences sharedPreferences = getSharedPreferences("MadMoney", Context.MODE_PRIVATE);

            if (!sharedPreferences.getBoolean("IsUserCreated", false)) {

                userCreateFragment = new UserCreateFragment();

                fragmentTransaction.replace(R.id.fragment_container, userCreateFragment).commit();

            } else {
                String userAddress = sharedPreferences.getString("UserAddressId", null);
                GlobalStatic.setUserAddressId(userAddress);

                walletFragment = new WalletFragment();
                receiverFragment = new ReceiverFragment();
                bucketFragment = new BucketFragment();

                fragmentTransaction.replace(R.id.bottom_fragment_container, walletFragment);

                fragmentTransaction.replace(R.id.middle_fragment_container, bucketFragment);

                fragmentTransaction.replace(R.id.top_fragment_container, receiverFragment);

                fragmentTransaction.commit();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void createUser(View view) {
        userCreateFragment.createUser(view);
    }

    //refresh money from the button/menu click
    @Override
    public void refreshMoney(View view) {
        walletFragment.refreshMoney();
    }

    //refresh money from bucket fragment
    @Override
    public void refreshMoney() {
        walletFragment.refreshMoneyView();
    }
}
