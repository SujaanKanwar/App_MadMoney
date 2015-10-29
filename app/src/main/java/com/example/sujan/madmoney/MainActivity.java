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
import android.widget.EditText;
import android.widget.TextView;

import com.example.sujan.madmoney.AppData.GlobalStatic;
import com.example.sujan.madmoney.Fragments.BucketFragment;
import com.example.sujan.madmoney.Fragments.ReceiverFragment;
import com.example.sujan.madmoney.Fragments.WalletFragment;
import com.example.sujan.madmoney.RegisterUser.RegisterUserActivity;
import com.example.sujan.madmoney.SharedConstants.Constants;


public class MainActivity extends AppCompatActivity implements WalletFragment.XMLClickables,
        NavigationView.OnNavigationItemSelectedListener {

    private WalletFragment walletFragment;
    private ReceiverFragment receiverFragment;
    private BucketFragment bucketFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeNavView();

        if (savedInstanceState == null) {

            SharedPreferences sharedPreferences = getSharedPreferences("MadMoney", Context.MODE_PRIVATE);

            if (!sharedPreferences.getBoolean("IsUserCreated", false)) {

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

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void initializeUserVariables(SharedPreferences sharedPreferences) {

        String userAddress = sharedPreferences.getString(Constants.USER_ADDRESS_ID, null);
        String userName = sharedPreferences.getString(Constants.USER_NAME, null);

        GlobalStatic.setUserAddressId(userAddress);

        TextView userNameView = (TextView) findViewById(R.id.user_name);
        TextView userAddressView = (TextView) findViewById(R.id.address_id);

        userNameView.setText(userName);
        userAddressView.setText(userAddress);
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

    private void activityRegisterNewUser() {

        Button registerMe = (Button) findViewById(R.id.register_me);

        registerMe.setVisibility(View.VISIBLE);

        final Context context = this;

        registerMe.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, RegisterUserActivity.class);

                startActivity(intent);
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
