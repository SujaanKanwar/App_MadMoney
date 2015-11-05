package com.example.sujan.madmoney;

import android.app.ActivityManager;
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
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.sujan.madmoney.AppData.GlobalStatic;
import com.example.sujan.madmoney.Fragments.BucketFragment;
import com.example.sujan.madmoney.Fragments.OfflineRFragment;
import com.example.sujan.madmoney.Fragments.OnlineRFragment;
import com.example.sujan.madmoney.Fragments.WalletFragment;
import com.example.sujan.madmoney.RegisterUser.RegisterUserActivity;
import com.example.sujan.madmoney.Services.BluetoothBackgroundService;
import com.example.sujan.madmoney.SharedConstants.SharedPrefConstants;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private FragmentTransaction fragmentTransaction;
    private WalletFragment walletFragment;
    private OfflineRFragment offlineRFragment;
    private BucketFragment bucketFragment;
    private OnlineRFragment onlineRFragment;
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

                runBackgroundBTService();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            case R.id.sign_off:
                stopBackgroundBTService();
                return true;

            case R.id.online_payment:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    setOnlinePaymentMode();
                }
                return true;

            case R.id.offline_payment:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    setOfflinePaymentMode();
                }
                return true;
            case R.id.refresh_money:
                refreshMoney();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        int id = menuItem.getItemId();

        Intent intent;

        if (id == R.id.nav_recharge) {

            intent = new Intent(this, RechargeActivity.class);

            startActivity(intent);

        } else if (id == R.id.nav_deposit_to_my_bank) {

            intent = new Intent(this, DepositInBankActivity.class);

            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_USER_REGISTERED && resultCode == RESULT_OK) {

            SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

            Button registerMe = (Button) findViewById(R.id.register_me);

            registerMe.setVisibility(View.GONE);

            initializeMainFragments();

            initializeUserVariables(sharedPreferences);

            runBackgroundBTService();
        }
    }

    private void runBackgroundBTService() {
     if(!isMyServiceRunning(BluetoothBackgroundService.class))
     {
         BluetoothBackgroundService bluetoothBackgroundService = new BluetoothBackgroundService("BluetoothListenService");
         Intent intent = new Intent(this,BluetoothBackgroundService.class);
         bluetoothBackgroundService.startService(intent);
     }
    }

    private void stopBackgroundBTService()
    {
        if(isMyServiceRunning(BluetoothBackgroundService.class))
        {
            BluetoothBackgroundService bluetoothBackgroundService = new BluetoothBackgroundService("BluetoothListenService");
            Intent intent = new Intent(this,BluetoothBackgroundService.class);
            bluetoothBackgroundService.stopService(intent);
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void refreshMoney() {
        if (walletFragment != null)
            walletFragment.refreshMoneyView();
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

        fragmentTransaction = getFragmentManager().beginTransaction();

        walletFragment = new WalletFragment();
        offlineRFragment = new OfflineRFragment();
        bucketFragment = new BucketFragment();
        onlineRFragment = new OnlineRFragment();

        fragmentTransaction.replace(R.id.bottom_fragment_container, walletFragment);

        fragmentTransaction.replace(R.id.middle_fragment_container, bucketFragment);

        fragmentTransaction.replace(R.id.top_fragment_container, offlineRFragment);

        fragmentTransaction.commit();
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

    private void setOfflinePaymentMode() {

        fragmentTransaction = getFragmentManager().beginTransaction();

        if (offlineRFragment == null)
            offlineRFragment = new OfflineRFragment();

        fragmentTransaction.replace(R.id.top_fragment_container, offlineRFragment).commit();
    }

    private void setOnlinePaymentMode() {

        fragmentTransaction = getFragmentManager().beginTransaction();

        if (onlineRFragment == null)
            onlineRFragment = new OnlineRFragment();

        fragmentTransaction.replace(R.id.top_fragment_container, onlineRFragment).commit();
    }
}
