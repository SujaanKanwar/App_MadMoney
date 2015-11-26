package com.example.sujan.madmoney.Services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.example.sujan.madmoney.AppData.GlobalStatic;
import com.example.sujan.madmoney.AppData.Money;
import com.example.sujan.madmoney.Connectors.UtilityConnector;
import com.example.sujan.madmoney.Resources.FileOperations;
import com.example.sujan.madmoney.SharedConstants.SharedPrefConstants;
import com.example.sujan.madmoney.Utility.MoneyStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UtilityService extends IntentService {
    private static final String ACTION_GET_APK_FILE = "com.example.sujan.madmoney.Services.action.GET_APK";
    private static final String ACTION_FETCH_MONEY = "com.example.sujan.madmoney.Services.action.FETCH_MONEY";

    private static final String ACTION_SEND_MONEY = "com.example.sujan.madmoney.Utility.action.SEND_MONEY";

    private static final String EXTRA_PARAM1 = "com.example.sujan.madmoney.Utility.extra.PARAM1";

    public static void getAPKFileFromServer(Context context) {
        Intent intent = new Intent(context, UtilityService.class);
        intent.setAction(ACTION_GET_APK_FILE);
        context.startService(intent);
    }

    public static void sendMoney(Context context, String userAddressId) {
        Intent intent = new Intent(context, UtilityService.class);
        intent.setAction(ACTION_SEND_MONEY);
        intent.putExtra(EXTRA_PARAM1, userAddressId);
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
            } else if (ACTION_SEND_MONEY.equals(action)) {
                String userAddressId = intent.getStringExtra(EXTRA_PARAM1);
                boolean result = transferMoney(userAddressId);
                if (result)
                    MoneyStore.deleteMoneyList(getApplicationContext(), GlobalStatic.getTransCollection());
            }
        }
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
            data.put("data",data1);
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
