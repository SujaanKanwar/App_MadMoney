package com.payment.sujan.madmoney;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.payment.sujan.madmoney.AppData.GlobalStatic;
import com.payment.sujan.madmoney.AppData.Position;
import com.payment.sujan.madmoney.Cryptography.KeyPairGeneratorStore;
import com.payment.sujan.madmoney.Fragments.BucketFragment;
import com.payment.sujan.madmoney.Fragments.OfflineRFragment;
import com.payment.sujan.madmoney.Fragments.OnlineRFragment;
import com.payment.sujan.madmoney.Fragments.WalletFragment;
import com.payment.sujan.madmoney.RegisterUser.RegisterUserActivity;
import com.payment.sujan.madmoney.Resources.DBTeleLocation;
import com.payment.sujan.madmoney.Services.BluetoothBackgroundService;
import com.payment.sujan.madmoney.Services.DeviceBackgroundServices;
import com.payment.sujan.madmoney.Services.UtilityService;
import com.payment.sujan.madmoney.SharedConstants.SharedPrefConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private FragmentTransaction fragmentTransaction;
    private WalletFragment walletFragment;
    private OfflineRFragment offlineRFragment;
    private BucketFragment bucketFragment;
    private OnlineRFragment onlineRFragment;
    private int NEW_USER_REGISTERED = 1;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

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

                runBackgroundServices();
            }
        }
    }

    private void runBackgroundServices() {
        runBackgroundBTService();

        statusCheck();
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        } else {
            buildGoogleApiClient();

            mGoogleApiClient.connect();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        buildGoogleApiClient();

                        mGoogleApiClient.connect();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }

    private String createTelPositionRQ(String city) {
        JSONObject jsonObject1 = new JSONObject();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("CurrentLocation", city);
            jsonObject1.put("data", jsonObject);
        } catch (JSONException e) {
        }
        return jsonObject1.toString();
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

            case R.id.sendDiscoveredLocation:
                sendDiscoveredLocations();
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

        switch (id) {
            case R.id.nav_recharge:
                intent = new Intent(this, RechargeActivity.class);

                startActivity(intent);
                break;
            case R.id.nav_deposit_to_my_bank:
                intent = new Intent(this, DepositInBankActivity.class);

                startActivity(intent);
                break;
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

    public void refreshMoney() {
        if (walletFragment != null)
            walletFragment.refreshMoney();
    }

    private void runBackgroundBTService() {
        String addressId = GlobalStatic.getUserAddressId();
        if (!isMyServiceRunning(BluetoothBackgroundService.class) && addressId != null) {
            BluetoothBackgroundService bluetoothBackgroundService = new BluetoothBackgroundService();
            bluetoothBackgroundService.startBTBackgroundService(getApplicationContext(), addressId);
        }
    }

    private void stopBackgroundBTService() {
        if (isMyServiceRunning(BluetoothBackgroundService.class)) {
            Intent intent = new Intent(this, BluetoothBackgroundService.class);
            getBaseContext().stopService(intent);
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

        final String userAddress = sharedPreferences.getString(SharedPrefConstants.USER_ADDRESS_ID, null);
        String userName = sharedPreferences.getString(SharedPrefConstants.USER_NAME, null);

        GlobalStatic.setUserAddressId(userAddress);

        TextView userNameView = (TextView) findViewById(R.id.user_name);
        TextView userAddressView = (TextView) findViewById(R.id.address_id);
        ImageButton fetchMoneyView = (ImageButton) findViewById(R.id.main_fetch_money);

        userNameView.setText(userName);
        userAddressView.setText("Copy Address");
        userAddressView.setTag(userAddress);
        final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        userAddressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData clip = ClipData.newPlainText("COPY_ADDRESS", userAddress);
                clipboard.setPrimaryClip(clip);
            }
        });

        fetchMoneyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (walletFragment != null)
                    walletFragment.fetchMoneyFromServer();
            }
        });
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
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

    private void sendDiscoveredLocations() {
        DBTeleLocation dbTeleLocation = new DBTeleLocation(this);
        List<Position> discoveredLocations = dbTeleLocation.selectTelPositions(2);
        if (discoveredLocations.size() > 0) {
            String requestData = createSendDiscoveredLocationRQ(discoveredLocations);
            ArrayList<String> requestIds = getRequestIds(discoveredLocations);
            UtilityService.sendDiscoveredTelLocations(this, requestData, requestIds);
        }
    }

    private ArrayList<String> getRequestIds(List<Position> discoveredLocations) {
        ArrayList<String> ids = new ArrayList<>();
        for (Position position : discoveredLocations) {
            ids.add(position.getRequestId());
        }
        return ids;
    }

    private String createSendDiscoveredLocationRQ(List<Position> discoveredLocations) {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        JSONArray locations = getJSONArrayOfDiscoverLocation(discoveredLocations);
        String userAddressId = GlobalStatic.getUserAddressId();
        String dataToSign = getDataStringToSign(discoveredLocations, userAddressId);
        String signature = KeyPairGeneratorStore.SignData(dataToSign);
        try {
            data.put("Locations", locations);
            data.put("UserAddressId", userAddressId);
            data.put("Signature", signature);
            result.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    private String getDataStringToSign(List<Position> discoveredLocations, String userAddressId) {
        String response = "";
        for (Position position : discoveredLocations) {
            response += position.getRequestId();
            response += position.getDateAndTimeOfDiscover();
        }
        response += userAddressId;
        return response;
    }

    private JSONArray getJSONArrayOfDiscoverLocation(List<Position> discoveredLocations) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject;
        for (Position position : discoveredLocations) {
            jsonObject = new JSONObject();
            try {
                jsonObject.put("RequestId", position.getRequestId());
                jsonObject.put("DateAndTimeOfDiscover", position.getDateAndTimeOfDiscover());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    public void refreshMoneyView() {
        if (walletFragment != null)
            walletFragment.refreshMoneyView();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        DBTeleLocation dbTeleLocation = new DBTeleLocation(this);
        Date currentLocalTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30")).getTime();
        String lastUpdatedDate = this.getApplicationContext()
                .getSharedPreferences(SharedPrefConstants
                        .SHARED_PREF_NAME, Context.MODE_PRIVATE)
                .getString(SharedPrefConstants.GET_TEL_POSITION_UPDATE_DATE, null);

        if (lastUpdatedDate == null)
            fetchTeleportingLocations(dbTeleLocation);
        else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(lastUpdatedDate));
            int lastUpdatedDayOfTheYear = calendar.get(Calendar.DAY_OF_YEAR);
            calendar.setTime(currentLocalTime);
            int todayDayOfTheYear = calendar.get(Calendar.DAY_OF_YEAR);
//            fetchTeleportingLocations(dbTeleLocation);
            if (todayDayOfTheYear - lastUpdatedDayOfTheYear > 0) {
                fetchTeleportingLocations(dbTeleLocation);
            } else {
                InitiateGeoFences();
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void fetchTeleportingLocations(DBTeleLocation dbTeleLocation) {
        dbTeleLocation.deleteAllPositions();
        double latitude = 0, longitude = 0;
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
            if (latitude != 0 || longitude != 0) {
                String cityName = null;
                Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> addresses = null;
                SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                try {
                    addresses = gcd.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    cityName = sharedPreferences.getString(SharedPrefConstants.GEO_CITY, "Pune");
                }
                if (addresses != null && addresses.size() > 0) {
                    cityName = addresses.get(0).getLocality();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SharedPrefConstants.GEO_CITY, cityName).commit();
                }
                String request = createTelPositionRQ(cityName);
                UtilityService.getTeleportingPositions(this.getApplicationContext(), request);
            }
        }
    }

    public void InitiateGeoFences() {
        DBTeleLocation dbTeleLocation = new DBTeleLocation(this);
        ArrayList<Position> positions = (ArrayList<Position>) dbTeleLocation.selectTelPositions(0); //0 for newly inserted locations
        if (positions.size() > 0) {
            GlobalStatic.setGoogleApiClient(mGoogleApiClient);
            DeviceBackgroundServices.setGeofences(this, positions);
        }
    }
}