package com.example.sujan.madmoney.Utility;

import android.content.Context;
import android.util.Base64;

import com.example.sujan.madmoney.AppData.APK;
import com.example.sujan.madmoney.Resources.FileOperations;
import com.example.sujan.madmoney.SharedConstants.SharedPrefConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sujan on 13/10/15.
 */
public class APKHandler {
    private Context context;

    public APKHandler(Context context) {
        this.context = context;
    }


    public RSAPublicKey getPublicKey(String madMoneyAddress) {
        RSAPublicKey pubKey = null;
        List<APK> apkList = getAPKList();
        String strPublicKey = searchPublicAddressInAPKList(apkList, madMoneyAddress);
        JSONObject jsonObject = getJsonObjectOfPublicKey(strPublicKey);

        try {
            BigInteger modulus = new BigInteger(1, Base64.decode(jsonObject.getString("MOD"), android.util.Base64.NO_WRAP));
//            BigInteger modulus = new BigInteger("136947486221534240336809045161119007686413202148356254256008242257807880011806249839958280499381781685533336502272005150411472772080103145719579964919695844030008227347783921988114770268121788586944627895411363116342557282723471473724106379478285639503016646161100296022146684339017261036439728847129902363197");
            BigInteger exponent = new BigInteger(Base64.decode(jsonObject.getString("EXP"), android.util.Base64.NO_WRAP));

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus,exponent);

//            byte[] publicBytes = Base64.decode(strPublicKey, android.util.Base64.NO_WRAP);
//
//            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            pubKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);

        } catch (Exception e) {
        }

        return pubKey;
    }

    private String searchPublicAddressInAPKList(List<APK> apkList, String madMoneyAddress) {
        int i = 0, j = 1;
        String[] inputArray = madMoneyAddress.split("/");
        while (i < inputArray.length && j < apkList.size()) {
            if (apkList.get(j).getValue().equalsIgnoreCase(inputArray[i])) {
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
        FileOperations fileOperations = new FileOperations(context, SharedPrefConstants.APK_FILE_NAME);
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

    public JSONObject getJsonObjectOfPublicKey(String strPublicKey) {
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(strPublicKey);
        }catch (JSONException e)
        {}
        return jObject;
    }
}
