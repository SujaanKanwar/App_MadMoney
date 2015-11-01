package com.example.sujan.madmoney;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.widget.Button;
import android.widget.TextView;

import com.example.sujan.madmoney.AppData.GlobalStatic;
import com.example.sujan.madmoney.Fragments.BucketFragment;
import com.example.sujan.madmoney.Fragments.ReceiverFragment;
import com.example.sujan.madmoney.Fragments.WalletFragment;
import com.example.sujan.madmoney.RegisterUser.RegisterUserActivity;
import com.example.sujan.madmoney.SharedConstants.SharedPrefConstants;


public class MainActivity extends AppCompatActivity implements WalletFragment.XMLClickables,
        NavigationView.OnNavigationItemSelectedListener {

    private WalletFragment walletFragment;
    private ReceiverFragment receiverFragment;
    private BucketFragment bucketFragment;

    private int NEW_USER_REGISTERED = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeNavView();

        if (savedInstanceState == null) {

            SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

            if (!sharedPreferences.getBoolean(SharedPrefConstants.IS_USER_CREATED, false)) {

                activityRegisterNewUser();

            } else {

                initializeMainFragments();

                initializeUserVariables(sharedPreferences);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void refreshMoney(View view) {
        walletFragment.refreshMoney();
    }

    //refresh money from bucket fragment
    @Override
    public void refreshMoney() {
        walletFragment.refreshMoneyView();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();
        Intent intent;
        if (id == R.id.nav_recharge) {
            intent = new Intent(this,RechargeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_deposit_to_my_bank) {
            intent = new Intent(this,DepositInBankActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void initializeUserVariables(SharedPreferences sharedPreferences) {

        String userAddress = sharedPreferences.getString(SharedPrefConstants.USER_ADDRESS_ID, null);
        String userName = sharedPreferences.getString(SharedPrefConstants.USER_NAME, null);

        GlobalStatic.setUserAddressId(userAddress);

        TextView userNameView = (TextView) findViewById(R.id.user_name);
        TextView userAddressView = (TextView) findViewById(R.id.address_id);

        userNameView.setText(userName);
        userAddressView.setText("Copy Address");
        userAddressView.setTag(userAddress);
    }

    private void initializeMainFragments() {

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        walletFragment = new WalletFragment();
        receiverFragment = new ReceiverFragment();
        bucketFragment = new BucketFragment();

        fragmentTransaction.replace(R.id.bottom_fragment_container, walletFragment);

        fragmentTransaction.replace(R.id.middle_fragment_container, bucketFragment);

        fragmentTransaction.replace(R.id.top_fragment_container, receiverFragment);

        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == NEW_USER_REGISTERED)
        {
            SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

            Button registerMe = (Button) findViewById(R.id.register_me);

            registerMe.setVisibility(View.GONE);

            initializeMainFragments();

            initializeUserVariables(sharedPreferences);
        }
    }

    private void activityRegisterNewUser() {

        Button registerMe = (Button) findViewById(R.id.register_me);

        registerMe.setVisibility(View.VISIBLE);

        final Context context = this;

        registerMe.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RegisterUserActivity.class);
                startActivityForResult(intent, NEW_USER_REGISTERED);
            }
        });
    }

    private void initializeNavView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
}
