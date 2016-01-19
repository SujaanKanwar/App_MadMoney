package com.payment.sujan.madmoney.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;

import com.payment.sujan.madmoney.AppData.GlobalStatic;
import com.payment.sujan.madmoney.AppData.Money;
import com.payment.sujan.madmoney.AppData.Position;
import com.payment.sujan.madmoney.Connectors.UtilityConnector;
import com.payment.sujan.madmoney.Resources.DBTeleLocation;
import com.payment.sujan.madmoney.Resources.FileOperations;
import com.payment.sujan.madmoney.SharedConstants.SharedPrefConstants;
import com.payment.sujan.madmoney.Utility.MoneyStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class UtilityService extends IntentService {
    private static final String ACTION_GET_APK_FILE = "com.payment.sujan.madmoney.Services.action.GET_APK";
    private static final String ACTION_TEL_POSITIONS = "com.payment.sujan.madmoney.Services.action.TEL_POSITIONS";
    private static final String ACTION_DISCOVERED_LOCATIONS = "com.payment.sujan.madmoney.Services.action.DISCOVERED_LOCATIONS";

    private static final String ACTION_SEND_MONEY = "com.payment.sujan.madmoney.Utility.action.SEND_MONEY";

    private static final String EXTRA_PARAM1 = "com.payment.sujan.madmoney.Utility.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.payment.sujan.madmoney.Utility.extra.PARAM2";

    public static void getAPKFileFromServer(Context context) {
        Intent intent = new Intent(context, UtilityService.class);
        intent.setAction(ACTION_GET_APK_FILE);
        context.startService(intent);
    }

    public static void sendMoney(Context context, String userAddressId) {
        Intent intent = new Intent(context, com.payment.sujan.madmoney.Services.UtilityService.class);
        intent.setAction(ACTION_SEND_MONEY);
        intent.putExtra(EXTRA_PARAM1, userAddressId);
        context.startService(intent);
    }

    public static void getTeleportingPositions(Context context, String request) {
        Intent intent = new Intent(context, UtilityService.class);
        intent.setAction(ACTION_TEL_POSITIONS);
        intent.putExtra(EXTRA_PARAM1, request);
        context.startService(intent);
    }

    public static void sendDiscoveredTelLocations(Context context, String requestData, ArrayList<String> requestIds) {
        Intent intent = new Intent(context, UtilityService.class);
        intent.setAction(ACTION_DISCOVERED_LOCATIONS);
        intent.putExtra(EXTRA_PARAM1, requestData);
        intent.putStringArrayListExtra(EXTRA_PARAM2, requestIds);
        context.startService(intent);
    }

    public UtilityService() {
        super("UtilityService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_APK_FILE.equals(action)) {

                String response = getAPKFileFromServer();

                storeAPKFile(response);
            } else if (ACTION_SEND_MONEY.equals(action)) {
                String userAddressId = intent.getStringExtra(EXTRA_PARAM1);
                boolean result = transferMoney(userAddressId);
                if (result)
                    MoneyStore.deleteMoneyList(getApplicationContext(), GlobalStatic.getTransCollection());
            } else if (ACTION_TEL_POSITIONS.equals(action)) {
                String requestData = intent.getStringExtra(EXTRA_PARAM1);
                List<Position> positions = getTeleportingPositionsFromServer(requestData);
                if (positions.size() > 0) {
                    storePositions(positions);
                    //set shared preference for today's date
                    Date currentLocalTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30")).getTime();
                    SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(SharedPrefConstants.GET_TEL_POSITION_UPDATE_DATE, currentLocalTime.toString()).commit();
                }
            } else if (ACTION_DISCOVERED_LOCATIONS.equals(action)) {
                String data = intent.getStringExtra(EXTRA_PARAM1);
                ArrayList<String> requestIds = intent.getStringArrayListExtra(EXTRA_PARAM2);
                String response = sendDiscoveredTelLocationsToServer(data);
                if (response.equalsIgnoreCase("true"))
                    updateDataBaseForDiscoveredLocations(requestIds);
            }
        }
    }

    private void updateDataBaseForDiscoveredLocations(ArrayList<String> requestIds) {
        DBTeleLocation dbTeleLocation = new DBTeleLocation(this);
        for (String id : requestIds) {
            dbTeleLocation.updateOperationStatus(id, -1);
        }
    }

    private String sendDiscoveredTelLocationsToServer(String data) {
        UtilityConnector utilityConnector = new UtilityConnector();
        return utilityConnector.sendDiscoveredLocations(data);
    }

    private void storePositions(List<Position> positions) {
        Context context = this.getApplicationContext();
        DBTeleLocation dbTeleLocation = new DBTeleLocation(context);
        dbTeleLocation.insertTelLocations(positions);
    }

    private List<Position> getTeleportingPositionsFromServer(String requestData) {
        UtilityConnector utilityConnector = new UtilityConnector();
        String serverResponseString = utilityConnector.getTeleportingPositions(requestData);
        return parseTeleportingResponse(serverResponseString);
    }

    private List<Position> parseTeleportingResponse(String serverResponseString) {
        List<Position> tPositions = new ArrayList<>();
        try {
            JSONObject jsonObjectResponse = new JSONObject(serverResponseString);
            if (jsonObjectResponse.getBoolean("IsSuccess")) {
                JSONArray locationArray = new JSONArray(jsonObjectResponse.getString("TLocations"));
                Position position;
                for (int i = 0; i < locationArray.length(); i++) {
                    position = new Position();
                    position.setLatitude(((JSONObject) locationArray.get(i)).getString("Latitude"));
                    position.setLongitude(((JSONObject) locationArray.get(i)).getString("Longitude"));
                    position.setRadius(((JSONObject) locationArray.get(i)).getString("Radius"));
                    position.setLocationName(((JSONObject) locationArray.get(i)).getString("LocationName"));
                    position.setCity(((JSONObject) locationArray.get(i)).getString("City"));
                    position.setDescription(((JSONObject) locationArray.get(i)).getString("Description"));
                    position.setRequestId(((JSONObject) locationArray.get(i)).getString("RequestId"));
                    position.setLoiteringTime(((JSONObject) locationArray.get(i)).getInt("LoiteringTime"));
                    position.setGeoTransactionType(((JSONObject) locationArray.get(i)).getString("GeofenceTransactionType"));
                    position.setExpirationTime(((JSONObject) locationArray.get(i)).getInt("ExpirationTime"));
                    tPositions.add(position);
                }
            }
        } catch (JSONException e) {
        }
        return tPositions;
    }

    private boolean transferMoney(String userAddressId) {
        if (GlobalStatic.getBucketCollection() == null)
            return false;

        GlobalStatic.setTransCollection(GlobalStatic.getBucketCollection());

        GlobalStatic.setBucketCollection(null);

        JSONArray money = Money.getJSONMoneyToTransferFromBucket();

        JSONObject data1 = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data1.put("UserAddressId", userAddressId);
            data1.put("MoneyList", money);
            data.put("data", data1);
        } catch (JSONException e) {
        }
        UtilityConnector utilityConnector = new UtilityConnector();

        return utilityConnector.transferMoney(data);
    }

    private String getAPKFileFromServer() {

        UtilityConnector utilityConnector = new UtilityConnector();

        return utilityConnector.getAPKFileFromServer();
    }

    private void storeAPKFile(String serviceResponse) {
        String apkTreeArray;
        Context context = this.getApplicationContext();

        try {
            JSONObject jsonObject = new JSONObject(serviceResponse);

            apkTreeArray = jsonObject.getJSONArray("APKTreeArray").toString();
        } catch (JSONException e) {
            return;
        }
        FileOperations fileOperations = new FileOperations(context, SharedPrefConstants.APK_FILE_NAME);
        if (apkTreeArray != null || apkTreeArray != "")
            fileOperations.write(apkTreeArray);
    }
}
