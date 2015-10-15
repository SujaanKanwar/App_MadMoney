package com.example.sujan.madmoney.Utility;

import android.content.Context;
import android.util.Base64;

import com.example.sujan.madmoney.AppData.APK;
import com.example.sujan.madmoney.Resources.FileOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sujan on 13/10/15.
 */
public class APKHandler {
    private Context context;
    private static String APK_FILE_NAME = "apkfile";

    public APKHandler(Context context) {
        this.context = context;
    }


    public RSAPublicKey getPublicKey(String madMoneyAddress) {
        RSAPublicKey pubKey = null;
        List<APK> apkList = getAPKList();
        String strPublicKey = searchPublicAddressInAPKList(apkList, madMoneyAddress);

        try {
            byte[] publicBytes = Base64.decode(strPublicKey, android.util.Base64.NO_WRAP);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            pubKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);

        } catch (Exception e) {
        }

        return pubKey;
    }

    private String searchPublicAddressInAPKList(List<APK> apkList, String madMoneyAddress) {
        int i = 0, j = 1;
        String[] inputArray = madMoneyAddress.split("/");
        while (i < inputArray.length && j < apkList.size()) {
            if (apkList.get(j).getValue().compareTo(inputArray[i]) == 0) {
                i++;
                j++;
            } else {
                if (apkList.get(j).getSiblingIndex() == -1)
                    return null;
                else
                    j = apkList.get(j).getSiblingIndex();
            }
        }
        return apkList.get(j - 1).getPublicKey();
    }

    public List<APK> getAPKList() {
        FileOperations fileOperations = new FileOperations(context, APK_FILE_NAME);
        String file = fileOperations.read();
        List<APK> apkList = new ArrayList<>();
        JSONArray apkArray = null;

        try {
            apkArray = new JSONArray(file);
        } catch (JSONException e) {
        }
        if (apkArray != null)
            for (int i = 0; i < apkArray.length(); i++) {
                APK apk = null;
                try {
                    JSONObject jsonObject = (JSONObject) apkArray.get(i);
                    String publicKey = jsonObject.getString("PublicKey");
                    int siblingIndex = jsonObject.getInt("SiblingIndex");
                    String value = jsonObject.getString("Value");
                    apk = new APK(publicKey, siblingIndex, value);
                    apkList.add(apk);
                } catch (JSONException e) {
                    break;
                }
            }
        return apkList;
    }
}
