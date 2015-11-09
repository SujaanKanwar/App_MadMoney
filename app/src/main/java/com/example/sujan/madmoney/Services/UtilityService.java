package com.example.sujan.madmoney.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.example.sujan.madmoney.Connectors.UtilityConnector;
import com.example.sujan.madmoney.Resources.FileOperations;
import com.example.sujan.madmoney.SharedConstants.SharedPrefConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class UtilityService extends IntentService {
    private static final String ACTION_GET_APK_FILE = "com.example.sujan.madmoney.Services.action.GET_APK";
    private static final String ACTION_FETCH_MONEY = "com.example.sujan.madmoney.Services.action.FETCH_MONEY";

    public static void getAPKFileFromServer(Context context) {
        Intent intent = new Intent(context, UtilityService.class);
        intent.setAction(ACTION_GET_APK_FILE);
        context.startService(intent);
    }

//    public static void fetchMoneyFromServer(Context context) {
//        Intent intent = new Intent(context, UtilityService.class);
//        intent.setAction(ACTION_FETCH_MONEY);
//        context.startService(intent);
//    }

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
            }
        }
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
        if(apkTreeArray != null || apkTreeArray != "")
            fileOperations.write(apkTreeArray);
    }

    private String getAPKFileFromServer() {

        UtilityConnector utilityConnector = new UtilityConnector();

        return utilityConnector.getAPKFileFromServer();
    }
}
